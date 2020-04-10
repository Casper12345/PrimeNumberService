namespace * com.server

typedef i32 PrimeNumber

struct PrimeList {
   1: list<PrimeNumber> primeNumbers;
}

service PrimeServerService {
    PrimeList getPrimeNumber(1: i32 input)
}


