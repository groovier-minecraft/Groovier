# Groovier
control minecraft servers with groovy scripts

## What is groovy?

check this out: https://groovy-lang.org/

## Getting Started

- download the plugins, and put into the plugins folder of your spigot/bungee server
- after run, the plugin folder will have list of folder which contain groovy scripts
- you can add new scripts, edit existing scripts, and delete scripts from the plugin folder
- after finished modification, do /groovier reload to reload the scripts

### Developing a Script

you can check the [wiki](ttps://github.com/groovier-minecraft/Groovier/wiki)

## Structures

```
Groovier/
    |- listeners ----------------------------- groovy scripts for event listener
        |- myListener.groovy 
    |- commands ------------------------------ groovy scripts for commands, can be multi level folder
        |- math ------------------------------ second level folder, usage will be /math <args>
            |- add.groovy -------------------- usage will be /math add
            |- minus.groovy ------------------ usage will be /math minus
        |- say.groovy ------------------------ single file, usage will be /say 
        |- info.groovy ----------------------- single file, usage will be /info
        |- love.groovy ----------------------- single file, usage will be /love
    |- lifecycles ---------------------------- groovy scripts for lifecycle, including plugin life cycle and script life cycle
        |- testLifeCycle.groovy -------------- will invoke method on lifecycle
    |- services ------------------------------ groovy scripts for services
        |- mathService.groovy ---------------- service for math, used for /math <args>
    |- arguments ----------------------------- groovy scripts for command arguments
        |- player.groovy --------------------- command argument for player
        |- offlineplayer.groovy -------------- command argument for offline player
    |- grapesConfig.groovy ------------------- groovy script for managing dependencies, you can add your extra dependencies here
    |- config.yml ---------------------------- configuration file
```

In the above structure, `listeners`, `arguments`, `commands`, `lifecycles` and `services` will be the root folders.

## How to use groovy script?

you can use like java code but with simplifier syntax by groovy, but import and package is still needed.

if you wish to use other library, you can use [@Grab / @Grapes annotation](http://docs.groovy-lang.org/latest/html/documentation/grape.html) 
to add dependency: 


## Other links

[javadocs](https://groovier-minecraft.github.io/Groovier/)

[wiki](https://github.com/groovier-minecraft/Groovier/wiki)


## Claimed

this plugin may not be production ready, if you find any problem from this plugin, feel free to report to me or post to the issue.

