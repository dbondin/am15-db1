#include "embedded-sql-test.h"

void do_commit() {
	EXEC SQL COMMIT;
}

#define SQLCHECK if(sqlca.sqlcode) { \
	do_commit(); \
	printf("SQLERROR: '%s' '%.5s'\n", sqlca.sqlerrm.sqlerrmc, sqlca.sqlstate);\
	return 1;\
	}

int db_connect(const char * password) {

	EXEC SQL BEGIN DECLARE SECTION;
	const char * sql_password = password;
	EXEC SQL END DECLARE SECTION;

	EXEC SQL CONNECT TO dbondin@gagarine USER dbondin IDENTIFIED BY :sql_password;
	SQLCHECK;
	
	EXEC SQL COMMIT;
	return 0;	
}

int db_get_cats_count(int * count) {

	EXEC SQL BEGIN DECLARE SECTION;
	int sql_count;
	EXEC SQL END DECLARE SECTION;
	
	EXEC SQL SELECT COUNT(*) INTO :sql_count FROM CAT;
	SQLCHECK;
	
	if(count != NULL) {
		*count = sql_count;
	}

	EXEC SQL COMMIT;
	return 0;
	
}

int db_get_cat(int cat_id, char * name, int name_len, int * age) {
	
	EXEC SQL BEGIN DECLARE SECTION;
	int sql_id = cat_id;
	char sql_name [32];
	int sql_age;
	int sql_age_ind;
	EXEC SQL END DECLARE SECTION;

	
	EXEC SQL SELECT NAME, AGE INTO :sql_name, :sql_age :sql_age_ind FROM CAT WHERE ID = :sql_id;
	SQLCHECK;
	
	if(name != NULL) {
		// Тут ошибка 100%
		strncpy(name, sql_name, name_len - 1);
		name[name_len - 1] = 0;
	}
	
	if(age != NULL) {
		if(sql_age_ind == 0) {
			*age = sql_age;
		}
		else {
			*age = -1;
		}
	}
	
	EXEC SQL COMMIT;
	return 0;
}

EXEC SQL BEGIN DECLARE SECTION;
	struct SQL_CAT {
		int id;
		char name[CAT_NAME_LEN];
		int age;
		int breed_id;
	};
	struct SQL_CAT_IND {
		int id;
		int name;
		int age;
		int breed_id;
	};
EXEC SQL END DECLARE SECTION;

void fill_cat(Cat * cat, struct SQL_CAT sql_cat, struct SQL_CAT_IND sql_cat_ind) {
	if(cat != NULL) {
		cat->id = sql_cat.id;
		strncpy(cat->name, sql_cat.name, CAT_NAME_LEN - 1);
		cat->name[CAT_NAME_LEN - 1] = 0;
		if(sql_cat_ind.age == 0) {
			cat->age = sql_cat.age;
			cat->age_is_null = 0;
		}
		else {
			cat->age_is_null = 1;
		}
		if(sql_cat_ind.breed_id == 0) {
			cat->breed_id = sql_cat.breed_id;
			cat->breed_id_is_null = 0;
		}
		else {
			cat->breed_id_is_null = 1;
		}
	}
}

int db_get_cat2(int cat_id, Cat * cat) {
	EXEC SQL BEGIN DECLARE SECTION;
	int sql_id = cat_id;
	struct SQL_CAT sql_cat;
	struct SQL_CAT_IND sql_cat_ind;
	EXEC SQL END DECLARE SECTION;

	EXEC SQL SELECT ID, NAME, AGE, BREED_ID INTO :sql_cat :sql_cat_ind FROM CAT WHERE ID = :sql_id;
	SQLCHECK;

	fill_cat(cat, sql_cat, sql_cat_ind);
	
	EXEC SQL COMMIT;
	return 0;
}

int db_get_all_cats(db_get_all_cats_callback callback, void * callback_arg) {
	
	Cat cat;
	
	EXEC SQL BEGIN DECLARE SECTION;
		struct SQL_CAT sql_cat;
		struct SQL_CAT_IND sql_cat_ind;
	EXEC SQL END DECLARE SECTION;

	EXEC SQL DECLARE db_get_all_cats CURSOR FOR
		SELECT ID,NAME,AGE,BREED_ID FROM CAT;
	SQLCHECK;
	
	EXEC SQL OPEN db_get_all_cats;
	SQLCHECK;
	
	while(1) {
		EXEC SQL FETCH db_get_all_cats INTO :sql_cat :sql_cat_ind;
		if(sqlca.sqlcode) {
			break;
		}
		fill_cat(&cat, sql_cat, sql_cat_ind);
		if(callback != NULL) {
			if(callback(&cat, callback_arg)) {
				break;
			}
		}
	}
	EXEC SQL CLOSE db_get_all_cats;
	SQLCHECK;
	
	EXEC SQL COMMIT;
	return 0;
}