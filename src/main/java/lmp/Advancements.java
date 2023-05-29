package lmp;

import java.util.ArrayList;
import java.util.List;

public class Advancements {

    public static List<Advancement> advancements = new ArrayList<>();

    public static void setAdvancements() {
        advancements.add(new Advancement("minecraft:adventure/adventuring_time", "Adventuring Time", "Discover every biome."));
        advancements.add(new Advancement("minecraft:adventure/kill_a_mob", "Monster Hunter", "Kill any hostile monster."));
        advancements.add(new Advancement("minecraft:adventure/kill_all_mobs", "Monsters Hunted", "Kill one of every hostile monster."));
        advancements.add(new Advancement("minecraft:adventure/root", "Adventure", "Kill any entity, or be killed by any entity."));
        advancements.add(new Advancement("minecraft:adventure/shoot_arrow", "Take Aim", "Using a bow or a crossbow, shoot an entity with an arrow, tipped arrow, or spectral arrow."));
        advancements.add(new Advancement("minecraft:adventure/sleep_in_bed", "Sweet Dreams", "Lie down in a bed. The advancement is granted as soon as the player is in the bed, even if the player does not successfully sleep."));
        advancements.add(new Advancement("minecraft:adventure/sniper_duel", "Sniper Duel", "Be at least 50 blocks away horizontally when a skeleton is killed by an arrow after the player has attacked it once."));
        advancements.add(new Advancement("minecraft:adventure/summon_iron_golem", "Hired Help", "Summon an iron golem."));
        advancements.add(new Advancement("minecraft:adventure/throw_trident", "A Throwaway Joke", "Hit a mob with a thrown trident."));
        advancements.add(new Advancement("minecraft:adventure/totem_of_undying", "Postmortal", "Activate a totem of undying by taking fatal damage."));
        advancements.add(new Advancement("minecraft:adventure/trade_at_world_height", "Sky Trader", "Trade with a Villager at the build height limit."));
        advancements.add(new Advancement("minecraft:adventure/very_very_frightening", "Very Very Frightening", "Hit a villager with lightning created by a trident with the Channeling enchantment."));
        advancements.add(new Advancement("minecraft:end/dragon_breath", "You Need a Mint", "Have a bottle of dragon's breath in your inventory."));
        advancements.add(new Advancement("minecraft:end/dragon_egg", "The Next Generation", "Have a dragon egg in your inventory."));
        advancements.add(new Advancement("minecraft:end/elytra", "Sky's the Limit", "Have a pair of elytra in your inventory."));
        advancements.add(new Advancement("minecraft:end/enter_end_gateway", "Remote Getaway", "Throw an ender pearl through, fly, or walk into an end gateway."));
        advancements.add(new Advancement("minecraft:end/find_end_city", "End Advancement", "Enter an end city."));
        advancements.add(new Advancement("minecraft:end/kill_dragon", "Free the End", "Kill the ender dragon."));
        advancements.add(new Advancement("minecraft:end/levitate", "Great View From Up Here", "Move a distance of 50 blocks vertically with the Levitation effect applied, regardless of whether it is caused by the effect."));
        advancements.add(new Advancement("minecraft:end/respawn_dragon", "The End... Again...", "Be in a certain radius from the exit portal when an ender dragon is summoned using end crystals."));
        advancements.add(new Advancement("minecraft:end/root", "End Advancement", "Enter the End dimension."));
        advancements.add(new Advancement("minecraft:husbandry/balanced_diet", "A Balanced Diet", "Eat one of every food item in the game."));
        advancements.add(new Advancement("minecraft:husbandry/obtain_netherite_hoe", "Serious Dedication", "Have a netherite hoe in your inventory."));
        advancements.add(new Advancement("minecraft:husbandry/bred_all_animals", "Two by Two", "Breed a pair of each breedable mob."));
        advancements.add(new Advancement("minecraft:husbandry/breed_an_animal", "Husbandry Advancement", "Breed a pair of a breedable mob."));
        advancements.add(new Advancement("minecraft:husbandry/plant_seed", "A Seedy Place", "Plant one crop."));
        advancements.add(new Advancement("minecraft:husbandry/root", "Husbandry Advancement", "Consume anything that can be consumed."));
        advancements.add(new Advancement("minecraft:husbandry/tactical_fishing", "Tactical Fishing", "Use a water bucket on any fish mob."));
        advancements.add(new Advancement("minecraft:husbandry/tame_an_animal", "Best Friends Forever", "Tame one tameable mob."));
        advancements.add(new Advancement("minecraft:nether/all_effects", "How Did We Get Here?", "Have all 26 effects applied at the same time."));
        advancements.add(new Advancement("minecraft:nether/all_potions", "A Furious Cocktail", "Have all 13 potion effects applied at the same time."));
        advancements.add(new Advancement("minecraft:nether/brew_potion", "Local Brewery", "Pick up an item from a brewing stand potion slot."));
        advancements.add(new Advancement("minecraft:nether/create_beacon", "Bring Home the Beacon", "Be within a 20×20×14 cuboid centered on a beacon block when it realizes it has become powered."));
        advancements.add(new Advancement("minecraft:nether/create_full_beacon", "Beaconator", "Be within a 20×20×14 cuboid centered on a beacon block when it realizes it is being powered by a size 4 pyramid."));
        advancements.add(new Advancement("eminecraft:nether/fast_travel", "Subspace Bubble", "Use the Nether to travel between 2 points in the Overworld with a minimum horizontal euclidean distance of 7000 blocks between each other, which is 875 blocks in the Nether."));
        advancements.add(new Advancement("minecraft:nether/find_fortress", "A Terrible Fortress", "Enter a Nether fortress."));
        advancements.add(new Advancement("minecraft:nether/get_wither_skull", "Spooky Scary Skeleton", "Have a wither skeleton skull in your inventory."));
        advancements.add(new Advancement("minecraft:nether/obtain_blaze_rod", "Into Fire", "Have a blaze rod in your inventory."));
        advancements.add(new Advancement("minecraft:nether/return_to_sender", "Return to Sender", "Kill a ghast using a ghast fireball."));
        advancements.add(new Advancement("minecraft:nether/root", "Nether Advancement", "Enter the Nether dimension."));
        advancements.add(new Advancement("minecraft:nether/summon_wither", "Withering Heights", "Summon the Wither."));
        advancements.add(new Advancement("minecraft:nether/uneasy_alliance", "Uneasy Alliance", "Kill a ghast while in the Overworld."));
        advancements.add(new Advancement("minecraft:story/cure_zombie_villager", "Zombie Doctor", "Throw a splash potion of Weakness at a zombie villager, feed it a golden apple, and wait for it to be cured."));
        advancements.add(new Advancement("minecraft:story/deflect_arrow", "Not Today, Thank You", "Block any projectile with a shield."));
        advancements.add(new Advancement("minecraft:story/enchant_item", "Enchanter", "Insert an item in an enchanting table, then apply an enchantment."));
        advancements.add(new Advancement("minecraft:story/enter_the_end", "The End?", "Enter the End dimension."));
        advancements.add(new Advancement("minecraft:story/enter_the_nether", "We Need to Go Deeper", "Enter the Nether dimension."));
        advancements.add(new Advancement("minecraft:story/follow_ender_eye", "Eye Spy", "Enter a stronghold."));
        advancements.add(new Advancement("minecraft:story/form_obsidian", "Ice Bucket Challenge", "Have a block of obsidian in your inventory."));
        advancements.add(new Advancement("minecraft:story/iron_tools", "Isn't It Iron Pick", "Have an iron pickaxe in your inventory."));
        advancements.add(new Advancement("minecraft:story/lava_bucket", "Hot Stuff", "Have a lava bucket in your inventory."));
        advancements.add(new Advancement("minecraft:story/mine_diamond", "Diamonds", "Have a diamond in your inventory."));
        advancements.add(new Advancement("minecraft:story/mine_stone", "Stone Age", "Mine stone with your pickaxe."));
        advancements.add(new Advancement("minecraft:story/obtain_armor", "Suit Up", "Have any type of iron armor in your inventory."));
        advancements.add(new Advancement("minecraft:story/root", "Minecraft", "Have a crafting table in your inventory."));
        advancements.add(new Advancement("minecraft:story/shiny_gear", "Cover Me with Diamonds", "Have any type of diamond armor in your inventory."));
        advancements.add(new Advancement("minecraft:story/smelt_iron", "Acquire Hardware", "Have an iron ingot in your inventory."));
        advancements.add(new Advancement("minecraft:story/upgrade_tools", "Getting an Upgrade", "Have a stone pickaxe in your inventory."));
        advancements.add(new Advancement("minecraft:adventure/voluntary_exile", "Voluntary Exile", "Kill a raider mob wearing an ominous banner."));
        advancements.add(new Advancement("minecraft:adventure/hero_of_the_village", "Hero of the Village", "Be in a certain radius from the village center when a raid ends in victory."));
        advancements.add(new Advancement("minecraft:adventure/spyglass_at_ghast", "Is it a Balloon?", "Look at a Ghast through a Spyglass."));
        advancements.add(new Advancement("minecraft:adventure/spyglass_at_parrot", "Is it a Bird?", "Look at a Parrot through a Spyglass."));
        advancements.add(new Advancement("minecraft:adventure/spyglass_at_dragon", "Is It a Plane?", "Look at the Ender Dragon through a Spyglass."));
        advancements.add(new Advancement("minecraft:adventure/bullseye", "Bullseye", "Hit the bullseye of a Target block from at least 30 meters away."));
        advancements.add(new Advancement("minecraft:adventure/honey_block_slide", "Sticky Situation", "Jump into a Honey Block to break your fall."));
        advancements.add(new Advancement("minecraft:adventure/ol_betsy", "Ol' Betsy", "Shoot a crossbow."));
        advancements.add(new Advancement("minecraft:adventure/lightning_rod_with_villager_no_fire", "Surge Protector", "Be within 30 blocks of a lightning strike that doesn't set any blocks on fire, while an unharmed villager is within or up to six blocks above a 30×30×30 volume centered on the lightning strike."));
        advancements.add(new Advancement("minecraft:adventure/fall_from_world_height", "Caves & Cliffs", "Free fall from the top of the world (build limit) to the bottom of the world and survive."));
        advancements.add(new Advancement("minecraft:nether/find_bastion", "Those Were the Days", "Enter a Bastion Remnant."));
        advancements.add(new Advancement("minecraft:nether/obtain_ancient_debris", "Hidden in the Depths", "Obtain Ancient Debris."));
        advancements.add(new Advancement("minecraft:nether/obtain_crying_obsidian", "Who is Cutting Onions?", "Obtain Crying Obsidian."));
        advancements.add(new Advancement("minecraft:nether/distract_piglin", "Oh Shiny", "Distract Piglins with gold."));
        advancements.add(new Advancement("minecraft:nether/ride_strider", "This Boat Has Legs", "Ride a Strider with a Warped Fungus on a Stick."));
        advancements.add(new Advancement("minecraft:nether/loot_bastion", "War Pigs", "Loot a chest in a Bastion Remnant."));
        advancements.add(new Advancement("minecraft:nether/use_lodestone", "Country Lode, Take Me Home", "Use a compass on a Lodestone."));
        advancements.add(new Advancement("minecraft:nether/netherite_armor", "Cover Me in Debris", "Get a full suit of Netherite armor."));
        advancements.add(new Advancement("minecraft:adventure/two_birds_one_arrow", "Two Birds, One Arrow", "Use a crossbow enchanted with Piercing to kill two phantoms."));
        advancements.add(new Advancement("minecraft:adventure/whos_the_pillager_now", "Who's the Pillager Now?", "Kill a pillager with a crossbow."));
        advancements.add(new Advancement("minecraft:adventure/arbalistic", "Arbalistic", "Kill five unique mobs with one Crossbow shot."));
        advancements.add(new Advancement("minecraft:adventure/fall_from_world_height", "Caves & Cliffs", "Free fall from the top of the world (build limit) to the bottom of the world and survive."));
        advancements.add(new Advancement("minecraft:adventure/play_jukebox_in_meadows", "Sound of Music", "While in a meadow biome, place down a jukebox and use a music disc on it."));
        advancements.add(new Advancement("minecraft:adventure/walk_on_powder_snow_with_leather_boots", "Light as a Rabbit", "Walk on powder snow while wearing leather boots."));
        advancements.add(new Advancement("minecraft:nether/charge_respawn_anchor", "Not Quite 'Nine' Lives", "Charge a Respawn Anchor to the maximum."));
        advancements.add(new Advancement("minecraft:nether/ride_strider_in_overworld_lava", "Feels Like Home", "While riding a strider, travel fifty blocks in lava in the Overworld."));
        advancements.add(new Advancement("minecraft:nether/explore_nether", "Hot Tourist Destinations", "Explore all Nether biomes."));
        advancements.add(new Advancement("minecraft:husbandry/safely_harvest_honey", "Bee Our Guest", "Use a Campfire to collect Honey from a Beehive using a Bottle without aggravating the bees."));
        advancements.add(new Advancement("minecraft:husbandry/wax_on", "Wax On", "Apply Honeycomb to a Copper Block."));
        advancements.add(new Advancement("minecraft:husbandry/breed_an_animal", "The Parrots and the Bats", "Breed two animals together."));
        advancements.add(new Advancement("minecraft:husbandry/ride_a_boat_with_a_goat", "Whatever Floats Your Goat", "Get in a Boat and float with a Goat."));
        advancements.add(new Advancement("minecraft:husbandry/complete_catalogue", "A Complete Catalogue", "Tame all cat variants."));
        advancements.add(new Advancement("minecraft:husbandry/tactical_fishing", "Fishy Business", "Catch a fish."));
        advancements.add(new Advancement("minecraft:husbandry/axolotl_in_a_bucket", "The Cutest Predator", "Catch an Axolotl in a Bucket."));
        advancements.add(new Advancement("minecraft:husbandry/kill_axolotl_target", "The Healing Power of Friendship!", "Have the Regeneration effect applied from assisting an axolotl or it killing a mob."));
        advancements.add(new Advancement("minecraft:husbandry/silk_touch_nest", "Total Beelocation", "Move a Bee Nest, with 3 bees inside, using Silk Touch."));
        advancements.add(new Advancement("minecraft:husbandry/make_a_sign_glow", "Glow and Behold", "Make the text of a sign glow."));
        advancements.add(new Advancement("minecraft:husbandry/allay_deliver_cake_to_note_block", "You've Got a Friend in Me", "Have an Allay drop a Cake at a Note Block."));
        advancements.add(new Advancement("minecraft:adventure/avoid_vibration", "Sneak 100", "Sneak near a Sculk Sensor or Warden to prevent it from detecting you."));
        advancements.add(new Advancement("minecraft:adventure/kill_mob_near_sculk_catalyst", "It Spreads", "Kill a mob near a Sculk Catalyst."));
        advancements.add(new Advancement("minecraft:husbandry/tadpole_in_a_bucket", "Bukkit Bukkit", "Catch a Tadpole in a Bucket."));
        advancements.add(new Advancement("minecraft:husbandry/allay_deliver_cake_to_note_block", "Birthday Song", "Have an Allay drop a Cake at a Note Block."));
        advancements.add(new Advancement("minecraft:husbandry/leash_all_frog_variants", "When the Squad Hops into Town", "Get each Frog variant on a Lead."));
        advancements.add(new Advancement("minecraft:husbandry/froglights", "With Our Powers Combined!", "Have all Froglights in your inventory."));


    }

    public static List<Advancement> getAdvancements() {
        return advancements;
    }

}
