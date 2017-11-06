#include <stdio.h>
#include <sql.h>
#include <sqlext.h>
#include <sqltypes.h>

int main(int argc, char **argv) {

	SQLHENV henv = NULL;

	if (SQLAllocHandle(SQL_HANDLE_ENV, NULL, &henv) == SQL_ERROR) {
		fprintf(stderr, "Error 1\n");
		return 1;
	}

	SQLSetEnvAttr(henv,
	SQL_ATTR_ODBC_VERSION, (void*) SQL_OV_ODBC3,
	SQL_IS_INTEGER);

	SQLHDBC hdbc = NULL;

	if (SQLAllocHandle(SQL_HANDLE_DBC, henv, &hdbc) == SQL_ERROR) {
		fprintf(stderr, "Error 2\n");
		return 1;
	}

	if (SQLConnect(hdbc, (SQLTCHAR*) "bird_db", SQL_NTS, (SQLTCHAR*) "",
	SQL_NTS, (SQLTCHAR*) "", SQL_NTS) == SQL_ERROR) {
		fprintf(stderr, "Error 3\n");
		return 1;
	}

	printf("Connection OK\n");

	SQLHSTMT hstmt;

	if (SQLAllocHandle(SQL_HANDLE_STMT, hdbc, &hstmt) == SQL_ERROR) {
		fprintf(stderr, "Error 4\n");
		return 1;
	}

	if (SQLPrepare(hstmt, (SQLCHAR *) "SELECT id, name, descr, canfly FROM bird", SQL_NTS) != SQL_SUCCESS) {
		fprintf(stderr, "Error 5\n");
		return 1;
	}

	SQLINTEGER sql_id;
	SQLVARCHAR sql_name[32];
	SQLVARCHAR sql_descr[256];
	SQLINTEGER sql_canfly;
	SQLLEN sql_descr_ind;

	SQLBindCol(hstmt,
			1, SQL_C_LONG, &sql_id,
			sizeof(sql_id), NULL);
	SQLBindCol(hstmt,
			2, SQL_C_CHAR, &sql_name,
			sizeof(sql_name), NULL);
	SQLBindCol(hstmt,
			3, SQL_C_CHAR, &sql_descr,
			sizeof(sql_descr), &sql_descr_ind);
	SQLBindCol(hstmt,
			4, SQL_C_LONG, &sql_canfly,
			sizeof(sql_canfly), NULL);

	if (SQLExecute(hstmt) !=  SQL_SUCCESS) {
		fprintf(stderr, "Error 6\n");
		return 1;
	}

	while(1) {
		if(SQLFetch(hstmt) == SQL_NO_DATA) {
			break;
		}
		printf("%ld %s %s %s\n", sql_id, sql_name, (sql_descr_ind != SQL_NULL_DATA) ? sql_descr : (SQLVARCHAR *) "<null>",
				sql_canfly ? "can fly" : "can not fly");
	}

	SQLCloseCursor(hstmt);

	SQLDisconnect(hdbc);

	SQLFreeHandle(SQL_HANDLE_DBC, hdbc);

	SQLFreeHandle(SQL_HANDLE_ENV, henv);

	return 0;
}
