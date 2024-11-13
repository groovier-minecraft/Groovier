package com.ericlam.mc.groovytest

import org.bukkit.Material
import org.junit.jupiter.api.Test

class TestGroovyDelegate {

    @Test
    void testGroovyDelegate() {
        def builder = new ItemBuilderImpl() as ItemBuilder
        builder.item = new Item()
        builder.meta = builder.item.meta

        builder.with {
            type = Material.DIAMOND
            displayName = "Hello Diamond!"
            lore = ["Hello", "Diamond!"]
        }

        println builder.item.toString()
    }


    trait ItemBuilder {
        @Delegate Item item
        @Delegate ItemMeta meta
    }

    class ItemBuilderImpl implements ItemBuilder {
        ItemBuilderImpl(){
            this.item = new Item()
            this.meta = item.meta
        }
    }


    class Item {
        private Material type = Material.STONE
        private ItemMeta meta = new ItemMeta()

        void setType(Material type) {
            this.type = type
        }

        ItemMeta getMeta() {
            return meta
        }


        @Override
        public String toString() {
            return "Item{" +
                    "type=" + type +
                    ", meta=" + meta +
                    '}';
        }
    }

    class ItemMeta {
        private String displayName;
        private List<String> lore;

        void setDisplayName(String displayName) {
            this.displayName = displayName
        }

        void setLore(List<String> lore) {
            this.lore = lore
        }


        @Override
        public String toString() {
            return "ItemMeta{" +
                    "displayName='" + displayName + '\'' +
                    ", lore=" + lore +
                    '}';
        }
    }
}
