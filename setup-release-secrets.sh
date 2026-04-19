#!/usr/bin/env bash
# One-time setup. Pushes the four secrets the release workflow needs into
# this GitHub repo via `gh`. Re-running is safe: gh overwrites existing
# values.
#
# Prerequisites:
#   * gh CLI installed and authenticated (`gh auth status`)
#   * Local GPG key generated (`gpg --list-secret-keys`)
#   * Central Portal user token created at:
#       https://central.sonatype.com/account
#
# After this finishes you should also publish the public key to a
# keyserver so Central can verify signatures:
#   gpg --send-keys <KEY_ID> --keyserver keys.openpgp.org

set -euo pipefail

if ! command -v gh > /dev/null; then
  echo "gh CLI not found. Install from https://cli.github.com/" >&2
  exit 1
fi

if ! gh auth status > /dev/null 2>&1; then
  echo "Not logged in to gh. Run: gh auth login" >&2
  exit 1
fi

echo "Available GPG secret keys:"
gpg --list-secret-keys --keyid-format=long
echo
read -rp "GPG key ID to use (long form, e.g. 2C51144F74D9EC68): " GPG_KEY_ID

echo "==> Exporting secret key and uploading as GPG_PRIVATE_KEY"
gpg --armor --export-secret-keys "$GPG_KEY_ID" | gh secret set GPG_PRIVATE_KEY

read -rsp "GPG passphrase: " GPG_PASS
echo
echo "==> Uploading MAVEN_GPG_PASSPHRASE"
printf '%s' "$GPG_PASS" | gh secret set MAVEN_GPG_PASSPHRASE
unset GPG_PASS

read -rp "Central Portal user-token USERNAME: " CENTRAL_USER
echo "==> Uploading CENTRAL_USERNAME"
printf '%s' "$CENTRAL_USER" | gh secret set CENTRAL_USERNAME

read -rsp "Central Portal user-token PASSWORD: " CENTRAL_PASS
echo
echo "==> Uploading CENTRAL_PASSWORD"
printf '%s' "$CENTRAL_PASS" | gh secret set CENTRAL_PASSWORD
unset CENTRAL_PASS

echo
echo "Done. Verify with: gh secret list"
echo
echo "Don't forget to publish your public key:"
echo "    gpg --send-keys $GPG_KEY_ID --keyserver keys.openpgp.org"
