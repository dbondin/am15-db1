#ifndef EMBEDDED_SQL_TEST_H_
#define EMBEDDED_SQL_TEST_H_

#include "cat.h"

int db_connect(const char * password);
int db_get_cats_count(int * count);
int db_get_cat(int cat_id, char * name, int name_len, int * age);
int db_get_cat2(int cat_id, Cat * cat);
/**
 * This function will be called once for each row from db_get_all_cats()
 * Returning 0 - continue fetching data, not 0 - stop fetching
 */
typedef int (*db_get_all_cats_callback)(const Cat * cat, void * arg);
int db_get_all_cats(db_get_all_cats_callback callback, void * callback_arg);
typedef enum age_compare_ops__ {
	NOT_USED, EQUAL, NOT_EQUAL, LESS, LESS_OR_EQUAL, GREATER, GREATER_OR_EQUAL
} age_compare_ops;
typedef struct cat_filter__ {
	int use_name; // if 0 - 'name' will not be used in search
	char name[CAT_NAME_LEN];
	age_compare_ops age_op; // if 'NOT_USED' age will not be used in search
	int age;
} cat_filter;
int db_get_filtered_cats(const cat_filter filter, db_get_all_cats_callback callback, void * callback_arg);
int db_get_filtered_cats2(const cat_filter filter, db_get_all_cats_callback callback, void * callback_arg);

#endif /* EMBEDDED_SQL_TEST_H_ */
