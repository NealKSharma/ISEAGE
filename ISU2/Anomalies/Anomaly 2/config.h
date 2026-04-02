#include <stdlib.h>

#define DETAILS  "https://en.wikipedia.org/wiki/Knight%27s_tour"
#define TIMETAKEN 18
#define MAX_RESULTS 100
#define MIN_RESULTS 10

struct tour {
    int num;
    char name[20];
};

struct tour my_tour = {1, "Knight's Tour"};