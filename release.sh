#!/usr/bin/env bash
# Cuts a Maven Central release.
#
# Local steps (this script):
#   1. Bumps the version across every POM
#   2. Bumps the reproducible build timestamp
#   3. Verifies the full reactor builds green
#   4. Commits, tags
#
# After this finishes, run:
#   git push origin master vX.Y.Z
#
# That tag push triggers .github/workflows/release.yml which signs the
# artifacts and uploads them to a Central Portal staging deployment.
# Final "Publish" click happens at https://central.sonatype.com/publishing
# (autoPublish is intentionally off for the first releases).

set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "usage: $0 <new-version>   e.g. $0 0.1.0" >&2
  exit 1
fi

NEW_VERSION="$1"
TAG="v${NEW_VERSION}"
TODAY=$(date -u +%Y-%m-%dT00:00:00Z)

if ! [[ "$NEW_VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[A-Za-z0-9.]+)?$ ]]; then
  echo "Version '$NEW_VERSION' is not a valid semver (e.g. 0.1.0 or 1.0.0-RC1)." >&2
  exit 1
fi

if ! git diff-index --quiet HEAD --; then
  echo "Working tree has uncommitted changes. Commit or stash first." >&2
  exit 1
fi

if git rev-parse -q --verify "refs/tags/${TAG}" > /dev/null; then
  echo "Tag ${TAG} already exists." >&2
  exit 1
fi

CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
if [[ "$CURRENT_BRANCH" != "master" ]]; then
  echo "Releases cut from 'master' only (currently on '$CURRENT_BRANCH')." >&2
  exit 1
fi

echo "==> Bumping version to ${NEW_VERSION} (all modules)"
mvn -B -q versions:set \
  -DnewVersion="${NEW_VERSION}" \
  -DgenerateBackupPoms=false \
  -DprocessAllModules=true

echo "==> Bumping reproducible build timestamp to ${TODAY}"
sed -i "s|<project.build.outputTimestamp>.*</project.build.outputTimestamp>|<project.build.outputTimestamp>${TODAY}</project.build.outputTimestamp>|" pom.xml

echo "==> Verifying full reactor (excluding gui)"
mvn -B verify -pl '!simulatest-gui'

echo "==> Committing version bump"
git add -u
git commit -m "release: ${NEW_VERSION}"

echo "==> Tagging ${TAG}"
git tag -a "${TAG}" -m "Release ${NEW_VERSION}"

cat <<EOF

Local prep complete.

Next:
    git push origin master ${TAG}

That triggers .github/workflows/release.yml which signs and stages the
release at https://central.sonatype.com/publishing — click 'Publish' there
to release publicly.
EOF
