# Voile

Voile is an addon for [Apoli](https://github.com/apace100/apoli) that provides new power, action and condition types.

## Documentation

If you'd like to use Voile in your Apoli or Origins powers, you can find the documentation [here](https://docs.reimaden.net/voile/introduction/).  
Prior experience with either mod is recommended. You can find the Origins documentation [here](https://origins.readthedocs.io/en/latest/).

## Including Voile in Your Project

### For Data Pack Developers

Using Voile for your data pack is easy. Simply download the latest jar for your version of Minecraft from the releases page,
[Modrinth](https://modrinth.com/mod/voile), or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/voile),
and include it in your instance's `mods` folder. You can then use the new types in your data pack.

Voile depends on Apoli, so make sure to grab that as well. Apoli is included with Origins.

### For Addon Developers

Add the required repositories to your build file:

```groovy
repositories {
    maven {
        name = "Nexus Repository Manager"
        url = 'https://oss.sonatype.org/content/repositories/snapshots'
    }
    // You should already have this one if you're making an Apoli/Origins addon
    maven {
        name = "JitPack"
        url = 'https://jitpack.io'
    }
}
```

Add the dependency:

```groovy
dependencies {
    modImplementation(include("com.github.Maxmani:voile:${project.voile_version}"))
}
```

## Licensing

Source code is distributed under the LGPLv3 license. See `COPYING` and `COPYING.LESSER` for details.