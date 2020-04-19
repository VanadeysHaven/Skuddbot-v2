# ALPHA-2.1.1 changelog
## Added
- [Currencies!](https://wiki.skuddbot.xyz/features/currencies)
  - Command: `!currency`.
    - View others currencies: `!currency <mention/userId>`.
    - Edit currencies: `!currency <mention/userId> <currency> <add/remove/set> <amount>`.
      - Requires `SERVER_ADMIN` permission.
  - ***NOTE:** There's only one currency type for now, but there will be more in the future!*
- Command: `!invte`
  - Prints a link to invite Skuddbot to your server and a link to join the test server!
 
## Changed
- User setting `STATS_PRIVATE` renamed.
  - Renamed to `PROFILE_PRIVATE`.
- Commands `!leaderboard` and `!lb` renamed.
  - Renamed to `!statsleaderboard` and `!slb`.

## Fixed
- Fixed a bug with permissions where the server owner did not automatically get the `SERVER_ADMIN` permission.