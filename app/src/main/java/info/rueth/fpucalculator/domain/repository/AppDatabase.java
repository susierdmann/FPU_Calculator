package info.rueth.fpucalculator.domain.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import info.rueth.fpucalculator.domain.model.Food;

/**
 * The database handler for all food items
 */
@Database(entities = {Food.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    static final String DB_NAME = "app_database";

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE food_table ADD amount_small INTEGER");
            database.execSQL("ALTER TABLE food_table ADD amount_medium INTEGER");
            database.execSQL("ALTER TABLE food_table ADD amount_large INTEGER");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE food_table ADD comment_small VARCHAR(20)");
            database.execSQL("ALTER TABLE food_table ADD comment_medium VARCHAR(20)");
            database.execSQL("ALTER TABLE food_table ADD comment_large VARCHAR(20)");
        }
    };

    public abstract FoodDao foodDao();
    private static volatile AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            //new PopulateDbAsync(INSTANCE).execute();
        }
    };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final FoodDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.foodDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();
            Food food = new Food();
            food.setName("Pizza");
            food.setFavorite(false);
            food.setCaloriesPer100g(229);
            food.setCarbsPer100g(24);
            food.setAmountSmall(160);
            food.setAmountMedium(220);
            food.setAmountLarge(320);
            food.setCommentSmall("1/2 kleine");
            food.setCommentMedium("1/2 große");
            food.setCommentLarge("Ganze kleine");
            mDao.insert(food);
            food = new Food();
            food.setName("Spaghetti");
            food.setFavorite(true);
            food.setCaloriesPer100g(162);
            food.setCarbsPer100g(32.6);
            food.setAmountSmall(0);
            food.setAmountMedium(0);
            food.setAmountLarge(0);
            mDao.insert(food);
            return null;
        }
    }
}


