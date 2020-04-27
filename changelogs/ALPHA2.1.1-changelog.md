# ALPHA-2.1.1 changelog
## Added
- [**Currencies!**](https://wiki.skuddbot.xyz/features/currencies)
  - Command: `!currency`.
    - View others currencies: `!currency <mention/userId>`.
    - Edit currencies: `!currency <mention/userId> <currency> <add/remove/set> <amount>`.
      - Requires `SERVER_ADMIN` permission.
  - **NOTE:** *There's only one currency type for now, but there will be more in the future!*
  - Currency leaderboards:
      - Command: `!currencyleaderboard`.
        - List available currencies: `!currencyleaderboard list`.
        - View currency leaderboard: `!currencyleaderboard <currencyName>`.
          - Defaults to `SKUDDBUX`.
- [**Daily bonuses!**](https://wiki.skuddbot.xyz/systems/daily-bonus)
  - Command: `!dailybonus`.
  - Users can now claim a daily bonus!
  - Bonuses scale up if a user claims multiple days in a row.
  - New stats:
    - `DAILY_CURRENT_STREAK`
    - `DAILY_LONGEST_STREAK`

- Command: `!invite`
  - Prints a link to invite Skuddbot to your server and a link to join the test server!
- New reaction "button" system in the backend.

## Changed
- User setting `STATS_PRIVATE` renamed.
  - Renamed to `PROFILE_PRIVATE`.
- Commands `!leaderboard` and `!lb` renamed.
  - Renamed to `!statsleaderboard` and `!slb`.
- Changed the layout of challenge.
- Reworked `!stats` command in the backend to support larger numbers.

## Fixed
- Fixed a bug with permissions where the server owner did not automatically get the `SERVER_ADMIN` permission.
- Fixed a bug where sometimes an error message for challenge didn't pop when it should have.
