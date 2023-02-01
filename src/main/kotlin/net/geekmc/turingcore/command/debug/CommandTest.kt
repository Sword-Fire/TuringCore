package net.geekmc.turingcore.command.debug

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.framework.AutoRegister
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandTest : Kommand({

    opSyntax {

    }


}, name = "test")