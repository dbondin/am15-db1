#include <stdio.h>
#include <unistd.h>
#include <string.h>

#include "embedded-sql-test.h"

int my_fn(const Cat * cat, void * not_used) {
	if (cat == NULL) {
		printf(">>> NULL\n");
	} else {
		printf(">>> %d %s %d %d\n", cat->id, cat->name,
				cat->age_is_null ? -1 : cat->age,
				cat->breed_id_is_null ? -1 : cat->breed_id);
	}
	return 0;
}

/**
 * Аргумент тут - номер строки
 */
int my_fn2(const Cat * cat, void * callback_args) {
	if(callback_args == NULL) {
		return 1;
	}
	else {
		int * line_no = (int *) callback_args;
		(*line_no)++;
		if(*line_no > 3) {
			return 1;
		}
	}
	if (cat == NULL) {
		printf("+++ NULL\n");
	} else {
		printf("+++ %d %s %d %d\n", cat->id, cat->name,
				cat->age_is_null ? -1 : cat->age,
				cat->breed_id_is_null ? -1 : cat->breed_id);
	}
	return 0;
}

int main(int argc, char **argv) {

	int status;
	int cat_count;
	char * password;
	char cat_name[32];
	int cat_age;
	Cat cat;
	int i;

	//???? Как прочитать пароль???
	password = getpass("Введите пароль: ");

	status = db_connect(password);
	if (status) {
		fprintf(stderr, "Error connecting to database\n");
		return 1;
	}

	status = db_get_cats_count(&cat_count);
	if (status) {
		fprintf(stderr, "Error getting cat count\n");
		return 1;
	}
	printf("Количество котов: %d\n", cat_count);

	status = db_get_cat(1, cat_name, sizeof(cat_name) / sizeof(cat_name[0]),
			&cat_age);
	if (status) {
		fprintf(stderr, "Error getting cat N1\n");
		return 1;
	}
	printf("Cat N1: %s %d\n", cat_name, cat_age);

	status = db_get_cat(4, cat_name, sizeof(cat_name) / sizeof(cat_name[0]),
			&cat_age);
	if (status) {
		fprintf(stderr, "Error getting cat N4\n");
		return 1;
	}
	printf("Cat N1: %s %d\n", cat_name, cat_age);

	for (i = 1; i < 10; ++i) {
		status = db_get_cat2(i, &cat);
		if (status) {
			fprintf(stderr, "Error getting cat2 N%d\n", i);
			break;
		}
		printf("Cat2 N%d: %d %s %d %d\n", i, cat.id, cat.name,
				cat.age_is_null ? -1 : cat.age,
				cat.breed_id_is_null ? -1 : cat.breed_id);
	}

	printf("*** Getting all cats\n");
	status = db_get_all_cats(my_fn, NULL);
	if (status) {
		fprintf(stderr, "Error getting all cats\n");
		return 1;
	}

	i=0;
	status = db_get_all_cats(my_fn2, (void *)&i);
	if (status) {
		fprintf(stderr, "Error getting all cats2\n");
		return 1;
	}

	printf("*** Getting filtered cats\n");
	{
		struct {
			char * str;
			age_compare_ops age_op;
		} map[] = {{"==", EQUAL}, {"!=", NOT_EQUAL}};
		char age_op[32] = { 0 };
		cat_filter filter = { 1, "Пушок", EQUAL, 123 };
		int i;
		printf("Enter filter NAME: ");
		fgets(filter.name, CAT_NAME_LEN, stdin);
		filter.name[strlen(filter.name) - 1] = 0;
		if(filter.name[0] == 0) {
			filter.use_name = 0;
		}
		else {
			filter.use_name = 1;
		}
		printf("Enter filter AGE OP: ");
		fgets(age_op, 32, stdin);
		age_op[strlen(age_op) - 1] = 0;
		filter.age_op = NOT_USED;
		for(i=0; i<sizeof(map)/sizeof(map[0]); ++i) {
			if(strncmp(age_op, map[i].str, 32) == 0) {
				filter.age_op = map[i].age_op;
				break;
			}
		}
		if(filter.age_op != NOT_USED) {
			printf("Enter filter AGE: ");
			scanf("%d", &(filter.age));
		}
		status = db_get_filtered_cats2(filter, my_fn, NULL);
		if (status) {
			fprintf(stderr, "Error getting filtered cats\n");
			return 1;
		}
		status = db_get_filtered_cats2(filter, my_fn, NULL);
		if (status) {
			fprintf(stderr, "Error getting filtered cats\n");
			return 1;
		}
	}

	return 0;
}
