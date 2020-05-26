# ALPHA-2.2 changelog

## Added
- **FREE FOR ALL**
  - You've been asking for it, you've been begging for it. It's back!
  - Details can be found on the wiki: [https://wiki.skuddbot.xyz/minigames/free-for-all](https://wiki.skuddbot.xyz/minigames/free-for-all)
- Weekly bonuses!
  - After hitting your cap on your daily bonus, you will gain a additional bonus every week!

## Changed
- `!stats` command
  - The stats command now has a strict arguments policy.
    - The command must be written with exactly 2, 3 or 5 arguments.
    - Any other amount will result in a immediate error.
  - Viewing: `!stats <category> [mention/id]`
  - Editing `!stats <mention/id> <stat> <add/remove/set> <amount>`
- Icon of challenge has been changed to :dagger:.

## Fixed
- A text bug in challenge.
- A null pointer exception in reactions being added.
- Fixed the TenMinuteTimer stopping after a exception.
  - I'm not sure what is causing the exception to be thrown, but I'll figure it out soon.