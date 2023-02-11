package net.geekmc.turingcore.command.debug

import net.geekmc.turingcore.library.di.turingCoreDi
import net.geekmc.turingcore.library.framework.AutoRegister
import net.geekmc.turingcore.util.extender.getLineOfSightEntity
import net.geekmc.turingcore.util.extender.help
import net.geekmc.turingcore.util.extender.onlyOp
import net.geekmc.turingcore.util.extender.onlyPlayer
import net.geekmc.turingcore.util.lang.Lang
import net.geekmc.turingcore.util.lang.sendLang
import net.minestom.server.command.builder.arguments.ArgumentLiteral
import org.kodein.di.instance
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
@Suppress("UnstableApiUsage")
object CommandInfo : Kommand({

    val handArg = ArgumentLiteral("hand")
    val blockArg = ArgumentLiteral("block")
    val entityArg = ArgumentLiteral("entity")
    val lang: Lang by turingCoreDi.instance()

    help {
        sender.sendLang(lang, "cmd.info.help")
    }

    syntax(handArg) {
        val item = player.itemInMainHand
        player.sendLang(
            lang, "cmd.info.hand",
            item.material(),
            item.toItemNBT().toString()
        )
    }.onlyOp().onlyPlayer()

    syntax(blockArg) {
        val pos = player.getLineOfSight(20)[0] ?: return@syntax
        val b = player.instance?.getBlock(pos) ?: return@syntax
        player.sendLang(
            lang, "cmd.info.block",
            b.registry().material().toString(),
            b.registry().namespace(),
            b.registry().blockEntity().toString(),
            b.nbt().toString()
        )
    }.onlyOp().onlyPlayer()

    syntax(entityArg) {
        val entity = player.getLineOfSightEntity(40.0)
        if (entity == null) {
            sender.sendLang(lang, "cmd.info.entity.noEntity")
            return@syntax
        }
        player.sendLang(lang, "cmd.info.entity",
            entity.entityType.toString(),
            entity.customName.toString(),
            entity.velocity,
            entity.gravityAcceleration,
            entity.gravityDragPerTick,
            entity.gravityTickCount,
            entity.entityMeta.toString()
        )
    }.onlyOp().onlyPlayer()
}, name = "information", aliases = arrayOf("info"))