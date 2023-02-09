package net.geekmc.turingcore.command.debug

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.color.message
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.getLineOfSightEntity
import net.geekmc.turingcore.util.extender.onlyPlayer
import world.cepi.kstom.command.arguments.literal
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
@Suppress("UnstableApiUsage")
object CommandInfo : Kommand({

    val hand by literal
    val block by literal
    val entity by literal

    help {

    }

    opSyntax(hand) {
        player.message("&y手中物品信息:")
        player.message("&rNbt: " + player.itemInMainHand.toItemNBT().toString())
    }.onlyPlayer()

    opSyntax(block) {
        player.message("&y指向方块信息:")
        val pos = player.getLineOfSight(20)[0] ?: return@opSyntax
        val b = player.instance?.getBlock(pos) ?: return@opSyntax
        player.message("&gMaterial: " + b.registry().material())
        player.message("&gNamespaceId: " + b.registry().namespace())
        player.message("&gBlockEntity: " + b.registry().blockEntity())
        player.message("&rNbt: " + b.nbt().toString())
    }.onlyPlayer()

    opSyntax(entity) {
        player.message("&y指向生物信息:")
        player.message("&rNbt: " + player.getLineOfSightEntity(20.0))
    }.onlyPlayer()
}, name = "information", aliases = arrayOf("info"))