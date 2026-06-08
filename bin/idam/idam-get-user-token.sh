#!/bin/sh
echo $(curl -s -X POST \
  "${3}/o/token" \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d "grant_type=password&username=${1}&password=${2}&client_id=${IDAM_CLIENT_ID:-webshow}&client_secret=${IDAM_CLIENT_SECRET:-AAAAAAAAAAAAAAAA}&scope=openid%20roles%20profile&redirect_uri=${IDAM_REDIRECT_URI:-http%3A%2F%2Flocalhost%3A8080%2Foauth2redirect}" \
  | jq -r '.access_token')
