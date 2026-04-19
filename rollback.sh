#!/usr/bin/env bash
# Rolls back a release that hasn't been published to Central yet.
#
# What it does:
#   1. Deletes the local git tag
#   2. Deletes the remote git tag (cancels CI if not yet started; prevents
#      re-deploys if it already finished)
#   3. Lists in-flight Central Portal deployments matching this version so
#      you can drop them. If CENTRAL_USERNAME/CENTRAL_PASSWORD are in your
#      environment, it can drop them for you with --drop.
#
# What it does NOT do:
#   - Touch the "release: X.Y.Z" commit on master. If you want it gone:
#       git revert HEAD          # safe, leaves history visible
#       git reset --hard HEAD~1  # destructive, only if not yet pushed
#   - Roll back an already-published Central release. Central artifacts
#     are immutable. Publish a new version instead.

set -euo pipefail

DROP=false
if [[ "${1:-}" == "--drop" ]]; then
  DROP=true
  shift
fi

if [[ $# -ne 1 ]]; then
  echo "usage: $0 [--drop] <version>   e.g. $0 0.1.0" >&2
  echo "       --drop  also delete matching Central staging deployments" >&2
  exit 1
fi

VERSION="$1"

if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9.]+)?$ ]]; then
  echo "Version '$VERSION' is not a valid semver (e.g. 0.1.0 or 1.0.0-RC1)." >&2
  exit 1
fi

TAG="v${VERSION}"
# Escaped for use inside an ERE pattern (so 1.2.3 doesn't also match 1x2x3).
VERSION_RE="${VERSION//./\\.}"

# --- 1. local tag ---------------------------------------------------------
if git rev-parse -q --verify "refs/tags/${TAG}" > /dev/null; then
  echo "==> Deleting local tag ${TAG}"
  git tag -d "${TAG}"
else
  echo "==> Local tag ${TAG} not present, skipping"
fi

# --- 2. remote tag --------------------------------------------------------
if git ls-remote --tags origin "${TAG}" 2>/dev/null | grep -q "refs/tags/${TAG}"; then
  echo "==> Deleting remote tag ${TAG}"
  git push --delete origin "${TAG}"
else
  echo "==> Remote tag ${TAG} not present, skipping"
fi

# --- 3. Central Portal staging --------------------------------------------
echo "==> Checking Central Portal for staged deployments of ${VERSION}"

if [[ -z "${CENTRAL_USERNAME:-}" || -z "${CENTRAL_PASSWORD:-}" ]]; then
  cat <<EOF

CENTRAL_USERNAME / CENTRAL_PASSWORD not in environment, so I can't query
the Central Portal API. Open this URL and drop any deployment for
'org.simulatest' at version ${VERSION}:

    https://central.sonatype.com/publishing/deployments

Droppable statuses: VALIDATED, FAILED.
PENDING and VALIDATING must finish processing before they can be dropped.
PUBLISHING and PUBLISHED are past the point of no return.

EOF
  exit 0
fi

# `base64 -w0` is GNU-only; pipe through tr for portability with BSD/macOS.
TOKEN=$(printf '%s:%s' "$CENTRAL_USERNAME" "$CENTRAL_PASSWORD" | base64 | tr -d '\n')
API="https://central.sonatype.com/api/v1/publisher"

# List all deployments. Propagate API failures explicitly: a curl error
# must not look like an empty result, otherwise callers (CI) treat
# transient failures as "nothing to drop" and silently move on.
if ! RESPONSE=$(curl -fsS -X POST -H "Authorization: Bearer ${TOKEN}" \
  "${API}/deployments?pageNum=0&pageSize=50"); then
  echo "Could not reach Central Portal API; check credentials/network." >&2
  exit 1
fi

# Parse without jq (not always installed). Pull deployment IDs whose name
# contains the version string.
MATCHES=$(echo "$RESPONSE" | grep -oE "\"deploymentId\":\"[^\"]+\"|\"deploymentName\":\"[^\"]+\"|\"deploymentState\":\"[^\"]+\"" \
  | paste -d, - - - \
  | grep -E "[:-]${VERSION_RE}([\",-]|$)" || true)

if [[ -z "$MATCHES" ]]; then
  echo "No Central staging deployment matched version ${VERSION}."
  exit 0
fi

echo
echo "Matching staged deployments:"
echo "$MATCHES"
echo

if ! $DROP; then
  echo "Re-run with --drop to delete them, or do it in the Central Portal UI:"
  echo "    https://central.sonatype.com/publishing/deployments"
  exit 0
fi

# Only VALIDATED and FAILED are droppable per the Central Portal API.
DROPPABLE=$(echo "$MATCHES" | grep -E '"deploymentState":"(VALIDATED|FAILED)"' || true)
if [[ -z "$DROPPABLE" ]]; then
  echo "No matched deployments are currently in a droppable state " >&2
  echo "(VALIDATED or FAILED). PENDING/VALIDATING must finish first." >&2
  exit 1
fi

while read -r ID; do
  echo "==> Dropping deployment ${ID}"
  if curl -fsS -X DELETE -H "Authorization: Bearer ${TOKEN}" \
    "${API}/deployment/${ID}"; then
    echo "    dropped"
  else
    echo "    failed to drop ${ID}" >&2
    exit 1
  fi
done < <(echo "$DROPPABLE" | grep -oE '"deploymentId":"[^"]+"' | sed 's/.*"\([^"]*\)"$/\1/')

echo
echo "Done. The 'release: ${VERSION}' commit on master is untouched —"
echo "revert it manually if you don't want it in the next release."
