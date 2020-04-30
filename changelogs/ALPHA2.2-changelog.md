# ALPHA-2.2 changelog
## Changed
- `!stats` command
  - The stats command now has a strict arguments policy.
    - The command must be written with exactly 2, 3 or 5 arguments.
    - Any other amount will result in a immediate error.
  - Viewing: `!stats <category> [mention/id]`
  - Editing `!stats <mention/id> <stat> <add/remove/set> <amount>`