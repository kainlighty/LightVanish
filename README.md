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

## › Commands and Permissions

| Command                         | Permission                       | Description                                          |
|---------------------------------|----------------------------------|------------------------------------------------------|
| lightvanish / lv                | lightvanish.use                  | Hide self ()                                         |
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
