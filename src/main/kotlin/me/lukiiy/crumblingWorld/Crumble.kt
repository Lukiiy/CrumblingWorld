package me.lukiiy.crumblingWorld

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class Crumble : JavaPlugin(), Listener {
    val radius = 5 // x2
    private var runnable: BukkitTask? = null
    private val updateTime = 1

    override fun onEnable() {
        runnable = object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.gameMode == GameMode.CREATIVE ||
                        player.gameMode == GameMode.SPECTATOR ||
                        player.isDead) continue

                    val loc = player.location
                    val (px, py, pz) = Triple(loc.blockX, loc.blockY, loc.blockZ)

                    for (x in (px - radius)..(px + radius)) {
                        for (y in (py - radius)..(py + radius)) {
                            for (z in (pz - radius)..(pz + radius)) {
                                val block = player.world.getBlockAt(x, y, z)
                                if (block.isEmpty ||
                                    block.isLiquid ||
                                    !block.getRelative(BlockFace.DOWN).isEmpty ||
                                    block.type.blastResistance > 100) continue

                                transform(block)
                                block.type = Material.AIR
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, updateTime * 20L)
    }

    override fun onDisable() {runnable!!.cancel()} // 2NotWorry

    fun transform(block: Block) {
        val fall = block.world.spawnFallingBlock(block.location.add(.5, 0.0, .5), block.blockData)
        fall.dropItem = false
        fall.setHurtEntities(true)
    }
}
