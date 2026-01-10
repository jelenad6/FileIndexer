Višenitno indeksiranje datoteka

Ova Java aplikacija implementira višenitno indeksiranje datoteka u zadatom direktorijumu koristeći producer/consumer model.
Producer niti rekurzivno obilaze direktorijume i pronalaze fajlove koji zadovoljavaju kriterijume: dozvoljene ekstenzije: .txt, .java, .md, maksimalna veličina fajla: 10 MB i ne indeksiraju se sakriveni fajlovi ili direktorijumi.
Consumer niti preuzimaju fajlove iz ograničene BlockingQueue i indeksiraju ih, pamteći apsolutnu putanju, veličinu, vreme poslednje izmene i ekstenziju.
Takodje, vodi se računa o statistici - broj pronađenih, indeksiranih i preskočenih fajlova.

Sinhronizacija i kontrola niti:

Poison pill - poseban File objekat koji signalizira consumer nitima da nema više fajlova.

CountDownLatch - osigurava da se finalni ispis statistike i indeksa izvrši tek kada sve producer i consumer niti završe.

BlockingQueue - thread-safe red sa ograničenim kapacitetom, sprečava race condition pri dodavanju i preuzimanju fajlova.

