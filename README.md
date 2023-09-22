# LightVanish

## Dependencies: LuckPerms

> #### **Optional: PlaceholderAPI**

## › Features:

1. #### Hiding by group weight: Multi-level invisibility support
2. #### Hiding in the online count in server list _for paper_
3. #### Hiding sounds and actions
   _Example: chest, block, doors sound(not everything yet and only paper), and etc..._
4. #### Prohibition on break of the specified blocks
5. #### Disabling invisibility in certain worlds

## › [API:](https://github.com/kainlighty/LightVanish/tree/main/src/main/java/ru/kainlight/lightvanish/API)

### _PlayerHideEvent:_

- #### getVanishedPlayers();
- #### getVanishedPlayer();
- #### getViewers();
- #### isTemporary();
- #### isVanished();

### _PlayerShowEvent_:

- #### getVanishedPlayers();
- #### getVanishedPlayer();
- #### isTemporary();
- #### isVanished();
- #### showAll();

### _Methods_

- #### LightVanishAPI.getAllVanished();
- #### LightVanishAPI.getVanishedPlayers();
- #### LightVanishAPI.getVanishedPlayer():
  #### player(), toggle(), hide(), show(), isTemporary(), getViewers()
- #### LightVanishAPI.isVanished();
- #### LightVanishAPI.showAll();

``` 
Check for invisibility without using API:

boolean isVanished(Player player) {
    return player.hasMetadata("vanished");
}
```
## › Placeholders:
- %LightVanish_isVanished%

## › Commands and Permissions

| Command                         | Permission                       | Description                                          |
|---------------------------------|----------------------------------|------------------------------------------------------|
| lightvanish / lv                | lightvanish.use                  | Hide self                                            |
| lightvanish <player\> (<time\>) | lightvanish.use.other            | Hide player or hide temporary, if time > 0           |
| lightvanish list                | lightvanish.list                 | See all vanished players                             |
| lightvanish show-all            | lightvanish.show-all             | Show all vanished players                            |
| lightvanish reload              | None                             | Reload all configurations _(only for console)_       |
| lightvanish reconfig            | None                             | Update all configurations _(only for console)_       |
| None                            | lightvanish.use.*                | None                                                 |
| None                            | lightvanish.silent.chest         | Allow chests to be opened quietly                    |
| None                            | lightvanish.silent.chest.editing | Allow editing silent chest                           |
| None                            | lightvanish.silent.*             | None                                                 |
| None                            | lightvanish.bypass.worlds        | Allow to remain invisible in the disabled-worlds     |
| None                            | lightvanish.bypass.world.<name\> | Allow you to remain invisible in the specified world |
| None                            | lightvanish.bypass.physical      | Allow pressure plates to be pressed and etc...       |
| None                            | lightvanish.bypass.place         | Allow blocks place in invisible                      |
| None                            | lightvanish.bypass.pickup        | Allow pickup items in invisible                      |
| None                            | lightvanish.bypass.*             | None                                                 |
| None                            | lightvanish.*                    | All rights                                           |

<details>
  <summary>MAIN CONFIGURATION</summary>

```
# Only when the server loading
update-notification: true

#English, Russian
language: English

abilities:
  prevent-join-quit-message: true #  !The change requires a server reboot
  # ---------------------------------------------------------------------
  # With the status FALSE in the server menu, hidden players will not be included in the online count
  # Paper or paper forks only
  include-in-online-count: true # ! The change requires a server reboot
  # ---------------------------------------------------------------------
  # Invisibility works by group weight
  # --- Example: If your weight is 10, then players with a weight less than 10 will not see you
  # Recommended = true
  by-group-weight: true # ! The change requires a server reboot
  # ---------------------------------------------------------------------
  animations: true  # ! The change requires a server reboot
  # ---------------------------------------------------------------------
  # Require default values;
  # complicated: false - change on spectator mode and back previous
  # complicated: true - fake synchronized inventory
  # interval - time to change previous gamemode or update interval for fake chest inventory
  #
  # Permissions:
  # lightvanish.silent.chest
  # lightvanish.silent.chest.editing
  # lightvanish.silent.chest.*
  silent-chest: # ! The change requires a server reboot
    complicated: false
    interval: 0
  # ---------------------------------------------------------------------
  # If the player moves to the specified worlds, then he comes out of invisibility
  # Except players with permission: lightvanish.bypass.world.<NAME> or lightvanish.bypass.worlds
  disabled-worlds: # ! The change requires a server reboot
    - 'test'
  # ---------------------------------------------------------------------
  # These blocks cannot be placed in vanished
  # Except players with permission: lightvanish.bypass.place
  blocked-place:
    - barrier
    - bedrock
    - diamond_ore
    - emerald_ore
    - golden_ore
    - golden_block
    - emerald_block
    - diamond_block
   ```
</details>

<details>
  <summary>MESSAGES</summary>

```
player-offline: "&c&l » &fPlayer <username> is offline"
player-not-found: "&c&l » &fPlayer <username> not found"

list:
header: " &m   &9&l VANISHED PLAYERS &r&m   "
body: "&8- &r<prefix> <username>"
footer: "&7 Total in vanish: <count>"
empty: "&c&mThere are no players in vanish"
time: "&e&lVANISHED TIME: <minutes>m. <seconds>s."

enable:
actionbar: "&9&lYOUR VANISHED"
self: "&9&l » &fYou have entered invisibility mode"
other:
sender: "&9&l » &fPlayer <username> is vanished"
player: "&b&l » &fStaff <username> enabled vanish for you"

disable:
all: "&a&l » &fAll the invisible have become visible :)"
self: "&9&l » &fYou are out of invisibility mode"
other:
sender: "&9&l » &fPlayer <username> unvanished"
player: "&b&l » &fStaff <username> disable vanish mode for you"
   ```
</details>
