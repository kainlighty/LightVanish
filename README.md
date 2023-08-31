# LightVanish

## 		`rgb(9, 105, 218)` Dependencies: LuckPerms, ProtocolLib
> **Optional: PlaceholderAPI**

## _Features:_
1. #### Hiding by group weight
2. #### Hiding in the online count (for paper)
3. #### Hiding sounds and actions (for example, pressing plates, opening chests and etc.)

## [_API_:](https://github.com/kainlighty/LightVanish/tree/main/src/main/java/ru/kainlight/lightvanish/API)
### Events:
> ### _PlayerHideEvent:_
>- setMessage(); // _Maybe null or changed_
>- getMessage(); // _Not null_
>- isVanished();
>- getVanishedPlayers();
>- getVanishedPlayer();
>
> #### and other default methods

> ### _PlayerShowEvent:_
>- setMessage(); // _Maybe null or changed_
>- getMessage(); // _Not null_
>- isVanished();
>- getVanishedPlayers();
>- getVanishedPlayer();
>- showAll();
>
> #### and other default methods

### Methods
``` 
Check for invisibility without using API:

boolean isVanished(Player player) {
    return player.hasMetadata("vanished");
}
```
> #### LightVanishAPI.getVanishedPlayers();
> #### LightVanishAPI.getVanishedPlayer();
> #### LightVanishAPI.showAll();
> #### LightVanishAPI.removePacketListeners();

## _Additionally:_
| Command              | Alias               | Permission            | Description                                  |
|----------------------|---------------------|-----------------------|----------------------------------------------|
| vanish               | /lv or /v           | lightvanish.use       | Hide self                                    |
| vanish <player\>     | /lv or /v <player\> | lightvanish.use.other | Hide player                                  |
| lightvanish list     | None                | lightvanish.list      | See invisible players                        |
| lightvanish show-all | None                | lightvanish.show-all  | Show all online players                      |
| lightvanish reload   | None                | lightvanish.reload    | Reload all configurations                    |
| lightvanish reconfig | None                | None                  | Update all configurations (only for console) |
| None                 | None                | lightvanish.pickup    | Allow pickup items in invisible              |
| None                 | None                | lightvanish.admin     | All rights                                   |
