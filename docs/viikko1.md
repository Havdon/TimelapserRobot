# Viikko 1

Suunnitelmani on rakentaa timelapse videoita kuvaava robotti, joka ottaa kuvat 
GoPro kameraa käyttäen ja jota ohjataan Android tabletistä. 

Ensimmäisellä viikolla aloitin GoPron ja tabletin välisen kommunikaation testaamisen. Ongelmaksi esiinty se että GoPron WiFi API ei ole avoin eikä sille ole dokumentaatiota. Onneksi kuitenkin netistä löytyi muide ihmisten listoja APIsta ja siitä miten se toimii. Lisäksi kulutin parisen tuntia etsiäkseni kirjastoa jolla pysyi näyttämään HTTP Live Stream videota tabletillä. Suurin este tässä oli oman kokemuksen puute Android devaamisessa. Sain sen kuitenkin toimivaan, vaikka delay onkin 2 sekunnin luokkaa. Tämän ei pitäisi olla suuri ongelma.

Kulutin lisäksi nelisen tuntia Android <-> Mindstorm bluetooth ytheyden luomiseen, mutta onnistuin siinä lopulta.

Aloitin lisäksi itse robootin rakentamisen, mikä on täysin uusi juttu minulle. Ilman aikaisempaa kokemusta, niin voin todeta että robotin suunnittelu ja mielessä hahmottelu on huomattavasti vaikeampaa kuin ohjelmiston suunnittelu!