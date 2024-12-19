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
                Bukkit.getOnlinePlayers().filter {
                    it.gameMode in listOf(GameMode.SURVIVAL, GameMode.ADVENTURE) && !it.isDead
                }.forEach {
                    val (px, py, pz) = with(it.location) { Triple(blockX, blockY, blockZ) }

                    (px - radius..px + radius).forEach { x ->
                        (py - radius..py + radius).forEach { y ->
                            (pz - radius..pz + radius).forEach { z ->
                                it.world.getBlockAt(x, y, z).takeIf { block ->
                                    block.isEmpty && !block.isLiquid && block.getRelative(BlockFace.DOWN).isEmpty && block.type.blastResistance <= 100
                                }?.apply { transform(this) }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, updateTime * 20L)
    }

    fun transform(block: Block) {
        block.world.spawnFallingBlock(block.location.add(0.5, 0.0, 0.5), block.blockData).apply {
            dropItem = false
            setHurtEntities(true)
        }
        block.type = Material.AIR
    }
}
