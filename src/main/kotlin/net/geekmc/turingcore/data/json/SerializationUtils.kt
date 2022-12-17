package net.geekmc.turingcore.data.json

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.EntityType
import net.minestom.server.instance.block.Block
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.potion.Potion
import net.minestom.server.potion.PotionEffect
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.NamespaceID
import net.minestom.server.utils.location.RelativeVec
import net.minestom.server.utils.math.IntRange
import org.jglrxavpok.hephaistos.nbt.NBT
import world.cepi.kstom.serializer.*
import java.time.Duration
import java.util.*

@Suppress("SpellCheckingInspection")
inline fun <reified T : @Serializable Any> isMinestomObject(obj: T) = isMinestomObject<T>()

@Suppress("SpellCheckingInspection")
inline fun <reified T : @Serializable Any> isMinestomObject(): Boolean {
    return when (T::class) {
        Block::class, RelativeVec::class,
        ParticleSerializer::class,
        BossBar::class,
        Component::class,
        Duration::class,
        EntityType::class,
        ItemStack::class,
        Potion::class,
        Material::class,
        NamespaceID::class,
        NBT::class,
        Pos::class,
        PotionEffect::class,
        SoundEvent::class,
        Sound::class,
        UUID::class,
        Vector::class,
        IntRange::class -> true
        else -> false
    }
}

@Suppress("SpellCheckingInspection")
inline fun <reified T : @Serializable Any> minestomSerializer(obj: T) = minestomSerializer<T>()

@Suppress("SpellCheckingInspection", "UNCHECKED_CAST")
@OptIn(InternalSerializationApi::class)
inline fun <reified T : @Serializable Any> minestomSerializer() = when (T::class) {
    Block::class -> BlockSerializer
    RelativeVec::class -> RelativeVecSerializer
    ParticleSerializer::class -> ParticleSerializer
    BossBar::class -> BossBarSerializer
    Component::class -> ComponentSerializer
    Duration::class -> DurationSerializer
    EntityType::class -> EntityTypeSerializer
    ItemStack::class -> ItemStackSerializer
    Potion::class -> PotionSerializer
    Material::class -> MaterialSerializer
    NamespaceID::class -> NamespaceIDSerializer
    NBT::class -> NBTSerializer
    Pos::class -> PositionSerializer
    PotionEffect::class -> PotionEffectSerializer
    SoundEvent::class -> SoundEventSerializer
    Sound::class -> SoundSerializer
    UUID::class -> UUIDSerializer
    Vector::class -> VectorSerializer
    IntRange::class -> IntRangeSerializer
    else -> T::class.serializer()
} as KSerializer<T>