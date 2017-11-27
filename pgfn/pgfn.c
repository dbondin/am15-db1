#include <postgres.h>
#include <fmgr.h>

#include <stdlib.h>
#include <string.h>

#ifdef PG_MODULE_MAGIC
PG_MODULE_MAGIC;
#endif

PG_FUNCTION_INFO_V1(add_one);

Datum
add_one(PG_FUNCTION_ARGS)
{
  int32   arg = PG_GETARG_INT32(0);

  PG_RETURN_INT32(arg + 1);
}

PG_FUNCTION_INFO_V1(check_host);

Datum
check_host(PG_FUNCTION_ARGS)
{
  text * host = PG_GETARG_TEXT_P(0);
  int32 result = 0;
  char cmd [1024] = { 0 };

  sprintf(cmd, "ping -c 1 '%s'", VARDATA(host));

  result = system(cmd);

  PG_RETURN_INT32(result);
}
