package net.geekmc.turingcore.library.data.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.contextual
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import org.jglrxavpok.hephaistos.nbt.NBT
import world.cepi.kstom.serializer.*

val TURING_CORE_SERIALIZATION_MODULE = SerializersModule {
    addMinestomSerializers(this)
}

@OptIn(ExperimentalSerializationApi::class)
fun addMinestomSerializers(builder: SerializersModuleBuilder) {
    builder.apply {
        contextual(BlockSerializer)
        polymorphicDefaultSerializer(Block::class) { BlockSerializer }
        polymorphicDefaultDeserializer(Block::class) { BlockSerializer }

        contextual(ItemStackSerializer)
        polymorphicDefaultSerializer(ItemStack::class) { ItemStackSerializer }
        polymorphicDefaultDeserializer(ItemStack::class) { ItemStackSerializer }

        contextual(PotionSerializer)

        contextual(NBTSerializer)
        polymorphicDefaultSerializer(NBT::class) { NBTSerializer }
        polymorphicDefaultDeserializer(NBT::class) { NBTSerializer }

        contextual(PositionSerializer)

        contextual(SoundSerializer)
        polymorphicDefaultSerializer(Sound::class) { SoundSerializer }
        polymorphicDefaultDeserializer(Sound::class) { SoundSerializer }

        contextual(VectorSerializer)
        contextual(IntRangeSerializer)
        contextual(UUIDSerializer)
        contextual(DurationSerializer)
        contextual(NamespaceIDSerializer)

        contextual(ComponentSerializer)
        polymorphicDefaultSerializer(Component::class) { ComponentSerializer }
        polymorphicDefaultDeserializer(Component::class) { ComponentSerializer }

        contextual(BossBarSerializer)
        polymorphicDefaultSerializer(BossBar::class) { BossBarSerializer }
        polymorphicDefaultDeserializer(BossBar::class) { BossBarSerializer }

        contextual(PermissionSerializer)
    }
}