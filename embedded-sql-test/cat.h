#ifndef CAT_H_
#define CAT_H_

#define CAT_NAME_LEN 32

typedef struct Cat_ {
	int id;
	char name[CAT_NAME_LEN];
	int age;
	int age_is_null;
	int breed_id;
	int breed_id_is_null;
} Cat;

#endif /* CAT_H_ */
