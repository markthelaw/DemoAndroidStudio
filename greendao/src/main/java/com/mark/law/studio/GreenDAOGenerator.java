package com.mark.law.studio;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDAOGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "demo");

        addNote(schema);

        new DaoGenerator().generateAll(schema, "../DemoAndroidStudio/app/src/main/java/greenDao");
    }

    private static void addNote(Schema schema) {
        Entity note = schema.addEntity("PictureLocation");
        note.addIdProperty();
        note.addStringProperty("name").notNull();
        note.addStringProperty("url").notNull();
//        note.addStringProperty("friendRegid").notNull();
    }
}
