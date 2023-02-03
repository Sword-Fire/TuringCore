package net.geekmc.turingcore.command.debug

import net.geekmc.turingcore.command.opSyntax
import net.geekmc.turingcore.library.framework.AutoRegister
import net.minestom.server.entity.EntityCreature
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.LivingEntity
import net.minestom.server.entity.Player
import net.minestom.server.entity.ai.goal.MeleeAttackGoal
import net.minestom.server.entity.ai.target.ClosestEntityTarget
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget
import net.minestom.server.utils.time.TimeUnit
import world.cepi.kstom.command.kommand.Kommand

@AutoRegister
object CommandTest : Kommand({

    opSyntax {

        val chicken = EntityCreature(EntityType.CHICKEN)

        val goals = listOf(
//            DoNothingGoal(chicken, 500, 0.1f),
            MeleeAttackGoal(chicken, 2.0, 500, TimeUnit.MILLISECOND),
//            RandomStrollGoal(chicken, 4)
        )
        val targets = listOf(
            LastEntityDamagerTarget(chicken, 15.0F),
            ClosestEntityTarget(chicken, 15.0) {
                it is Player
            }
        )

        chicken.setGravity(.0,0.5)
        chicken.addAIGroup(goals, targets)
        chicken.setInstance(player.instance!!, player.position)

//        chicken.addAIGroup(
//            listOf(
//                DoNothingGoal(chicken, 500, 0.1f),
//                MeleeAttackGoal(chicken, 2.0, 500, TimeUnit.MILLISECOND),
//                RandomStrollGoal(chicken, 4)
//            ),
//            listOf(
//                LastEntityDamagerTarget(chicken, 15.0F),
//                ClosestEntityTarget(
//                    chicken, 15.0, Player.javaClass)
//                )
//            )
//        )


//                    final Instance instance = ...; // Instance to spawn the chicken in
//        final Pos spawnPosition = new Pos (0, 42, 0);
//        chicken.setInstance(instance, spawnPosition);
    }.onlyPlayers()


}, name = "test") {

    class MyChicken(type: EntityType) : LivingEntity(type) {

    }

}