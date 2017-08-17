# HRadminJSFPF

A web applikáció készítésénél igyekeztem a szükséges elemeket (fejlesztő környezet, server, frameworkok, programozási nyelvek, JAVA EE API-ok) minimalizálni és igyekeztem olyan megoldást keresni, amivel a legjobban hasznosítható a már megszerzett SE tudás. Kezdésnek ez tűnt a legjobb választásnak. A Netbeans EE-re szabott változatát töltöttem le, a vele együtt települt Glassfish szervert választottam, Spring frameworkot nem alkalmaztam, Netbeans-ben elérhető Java DB-vel dolgoztam, arra replikáltam a kurzus során használt Oracle HR schemat. A Java EE platformból alapvetően csak a JSF és EJB API-kat használtam, illetve a JPA-t annyiban, amennyiben a Hibernate is alkalmazza. A webes view-hoz a viszonylag könnyen használható és állítólag legelterjedtebb Primefaces frameworkot használtam fel. Primefaces-hez hasonló frameworkből számos rendelkezésre áll, valamelyik szükséges (kezdetben mindenképpen), mert önmagában a JSF csak alapokat nyújt, rengeteg kódolással lehet azokat a funkcionalitásokat, komponenseket megvalósítani, ami egy valamirevaló viewhoz szükséges. A frameworkok out-of-box kínálják ezeket. HTML5, CSS, Javascript, JQuery tudás alapszinten még szükséges, hogy a view végül többé-kevésbé megfeleljen az elvárásainknak. Egy esetben (autocomplete) Primefaces helyett a JQuery autocomplete-jét használtam, hogy jobban az igényeimre szabhassam. A kiváltó ok az volt, hogy csökkentsem a serverrel való kommunikációt, ne a szerver adja minden billentyű lenyomásnál a találatokat, hanem kliens-oldalon történjen minden. Nem biztos, hogy ez a jó megoldás, mivel így akár rengeteg adat is kerülhet egy kliens oldali változóba, mindenesetre JQuery és AJAX tanulásnak tökéletes és a HR Schema méreteinek megfelel.

Netbeans EE változatának letöltése

JDBC resource konfigurálása Glassfish szerveren
GlassFish 4.1.1 alatt (ez a verzió jön Netbeans-szel) nem működik a server konzolon az új elem hozzáadása. Config fileba (../glassfis/domains/domain1/config/domain.xml) lehet beírni az alábbi sorokat a megfelelő helyekre. A végén (szerver újraindítás után, ha szükséges) a hozzáadott jdbc/hr resource mind a JDBC connection, mind Hibernate esetén használható lesz, adott esetben a szerver gondoskodik a sessionök kezeléséről. 
1. 
<jdbc-resource pool-name="HRPool" jndi-name="jdbc/hr"></jdbc-resource>
2.
<jdbc-connection-pool is-isolation-level-guaranteed="false" datasource-classname="org.apache.derby.jdbc.ClientDataSource" name="HRPool" res-type="javax.sql.DataSource">
      <property name="User" value="HR"></property>
      <property name="DatabaseName" value="HR"></property>
      <property name="serverName" value="localhost"></property>
      <property name="PortNumber" value="1527"></property>
      <property name="URL" value="jdbc:derby://localhost:1527/HR"></property>
      <property name="Password" value="hr"></property>
</jdbc-connection-pool>
3.
<resource-ref ref="jdbc/hr"></resource-ref>

Adatbázis konfigurálása
Nincs Oracle adatbázis a gépemen, így a megszokott adatbázis helyett a Netbeans-szel együtt feltelepülő Java/Derby DB-t használom. Nem próbáltam, de Oracle adatbázissal is kell működnie a projekteknek a megfelelő paraméterek átírása (driver, URL) után. Az alábbi lépések azt mutatják be, hogyan lehet a beépített Derby adatbázist az Oracle HR scheméhoz hasonlóan beállítani.
1. Netbeans Services fül/Databases/Java	DB. -> Create Database 'HR' névvel, 'HR' user, 'hr' jelszó. Utána a fülön alább megjelenik az új adatbázis a URL nevével, amivel a programunkból meg tudjuk szólítani. 
2. HR schema előállítása az adatbázison.
Connect -> Execute command: Renato Rodrigues-nek köszönhetően létre tudjuk hozni és feltölteni a HR schemat (egyszerűbb triggerek nélküli változatát) a Derbyre.
ld. https://www.linkedin.com/pulse/oracle-hr-schema-apache-derby-renato-rodrigues
A futtatandó sql-ek: https://github.com/rs-renato/derby-hr-schema
HR_CREATE.sql:
Az adatbázisok közti egyszerűbb "átjárhatóság" kedvéért futtatás előtt javasolt az EMPLOYEES tábla phone_NUMERIC mezőjét, PHONE_NUMBER-re átírni.
HR_POPULATE.sql:
Ahol APP. szerepel a tábla neve előtt, ki kell törölni.

Securityhez adatbázis és server konfigurálása
(Megj:minden próbálkozás ellenére nem működik, az alábbi hivatkozások egy az egyben történő implementálása esetén sem. Helyette a a glassfish file realm-jét használom.)
ld: http://bart-kneepkens.github.io/using-a-glassfish-4-jdbcrealm-to-secure-your-web-application/index.html vagy https://javatutorial.net/glassfish-form-based-authentication-example
Create jdbcrealm db tables.sql és még
<jdbc-resource pool-name="SecurityPool" jndi-name="jdbc/security"></jdbc-resource>
<jdbc-connection-pool is-isolation-level-guaranteed="false" datasource-classname="org.apache.derby.jdbc.ClientDataSource" 		name="SecurityPool" res-type="javax.sql.DataSource">
	<property name="User" value="glassfish"></property>
	<property name="DatabaseName" value="glassfishrealm"></property>
	<property name="serverName" value="localhost"></property>
	<property name="PortNumber" value="1527"></property>
	<property name="URL" value="jdbc:derby://localhost:1527/GLASSFISH"></property>
	<property name="Password" value="admin"></property>
</jdbc-connection-pool>
<resource-ref ref="jdbc/glassfishrealm"></resource-ref>


Felhasznált dokumentumok:
https://docs.oracle.com/javaee/7/tutorial/
https://www.primefaces.org/docs/guide/primefaces_user_guide_6_1.pdf
https://www.primefaces.org/showcase/
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_sinle/Hibernate_User_Guide.html
https://db.apache.org/derby/docs/10.13/ref/
http://learn.jquery.com/
https://jqueryui.com/
https://www.w3schools.com/js/default.asp
https://javatutorial.net/glassfish-form-based-authentication-example
https://www.tutorialspoint.com/jsp/jsp_standard_tag_library.htm



Megválaszolatlan kérdések és problémák:
- Security
JDBCRealm változatot nem tudtam működésre bírni, helyette a file realmet használom. Ez semmiképpen nem ok, meg kell oldani. Derby helyett meg kellene próbálni más adatbázis szerverrel. 
Az egész security kicsit bizonytalan. EE tutorial alapján nem tudtam belőle többet kihozni. Nem ok, hogy bármennyiszer próbálkozhat, lockolni kellene 3 próbálkozás után a login nevet (ehhez kellene a jdbc realm a file helyett). 
Arra az esetre, ha Logout Session timeout miatt következik be, saját megoldást írtam. Egyszerűbb, mint a neten találtak, viszont pont az egyedisége miatt gyanús, hogy messze nem best practice, lehetnek olyan mellékhatásai, amit a korlátozott tudásom miatt nem látok.
- Az Autocomplete-ra alkalmazott Servlet használata esetén kilépésnél, session.invalidate-nél a container kivételeket dob. Bean leak és hasonló rondaságok (részletes kivételeket ld lent), amik a felhasználó oldalán nem okoznak semmilyen működési hibát, de valószínűleg a háttérben memória/teljesítmény problémák lehetnek, intenzív használat, sok felhasználó esetén lehet, hogy észrevehetően. Nem értem, miért nem segített, ha a logout-nál az általam kreált szerverre (amit, ha el se indítok nincs kivétel) ráküldök egy destroyt.
Úgy tűnik, nem lehet megoldani: https://bz.apache.org/bugzilla/show_bug.cgi?id=57314. Meg kellene próbálni más EE szerverrel.
- Servletekkel általában meggyűlik a bajom. Ha JSF-Facelet alapot kiegészítem úgy, hogy JQuery-AJAX kommunikál egy külön servlettel, ami kommunikál a beanekkel is, valami furcsaság mindig történik. Első körben a Logint is egy külön servlettel oldottam meg, azon keresztül adtam át a bean-nek a user nevét. Ez is működött logout-ig, utána az újbóli loginnél a Push Servlettel összeakadt, illetve a Login Servletet a második loginnél már nem tudta meghívni. Ez is kapott destroyt, nem segített. Nem találtam adekvát választ arra, hogy miért nem szereti EE, ha a Facelet Servlet mellett más servletet is írok, vagy ha abba bean-t injektálok, vagy hogyan kellene másképp megírnom, hogy elfogadja. Józan ésszel csak érthetetlen, miért ne lehetne ilyent csinálni, sok plusz mozgásteret ad a webes funkciók bővítésére, ha javascripten keresztül is tudok kommunikálni. 
- Ha módosításkor egy cella értékét listából tudjuk kiválasztani, Primefaces minden sorra feltölti az összes lehetséges értéket az adattábla betöltésekor. Ha az adattábla betöltése után megnézzük az oldal forrását (egy frissítés nyomását követően), akkor látjuk, hogy ahány sor, annyiszor betöltve a teljes adatlista, szörnyen néz ki. Ez nagyon amatőrnek hat, valószínűleg nagy számú sor esetén komolyan romlik is a teljesítmény. Mindig csak arra a sorra kellene betölteni a lehetséges adatokat, amit szerkesztésre megnyitunk. Próbáltam JQuery-vel betölteni az adatokat, de Primefaces validációs hibára fut, ha az adattábla betöltésekor nem töltöm be az adatokat. Lazy loading-et tettem az adattáblára, legalább így mindig csak egy oldalnyi adatot tölt.
- Formokkal kapcsolatban elvárásként határozzák meg, hogy strukturálni kell, ne egy mindenható form legyen az oldalon. Nem tudtam megfelelni, ha minden más ok lett volna, végül a p:layout nem engedte, hogy a unitokra külön form legyen, csak úgy hajlandó működni, ha az egészet egy formba ágyazom. Partialsubmit-tal ellensúlyozom ezt az állítólagos hibát. (https://stackoverflow.com/questions/25339056/understanding-primefaces-process-update-and-jsf-fajax-execute-render-attributes).
- Ha dinamikusan építjük fel a Datatable-t, a validáció nem tud az oszloptól függő adatokból dolgozni. A validáció korábban "fut", csak statikus módon lehet használni. A required metódust tudjuk használni oszlopoktól függően is.
https://stackoverflow.com/questions/3442380/jstl-cif-doesnt-work-inside-a-jsf-hdatatable (hasonló problémáról szól)



Kivételek logout esetén
[2017-06-27T18:57:06.504+0200] [glassfish 4.1] [SEVERE] [AS-WEB-CORE-00037] [javax.enterprise.web.core] [tid: _ThreadID=98 _ThreadName=http-listener-2(1)] [timeMillis: 1498582626504] [levelValue: 1000] [[
  An exception or error occurred in the container during the request processing
java.lang.NullPointerException
	at org.glassfish.grizzly.http.util.ByteChunk.append(ByteChunk.java:431)
	at org.apache.catalina.authenticator.FormAuthenticator.saveRequest(FormAuthenticator.java:615)
	at org.apache.catalina.authenticator.FormAuthenticator.authenticate(FormAuthenticator.java:250)
	at com.sun.web.security.RealmAdapter.invokeAuthenticateDelegate(RealmAdapter.java:1524)
	at org.apache.catalina.authenticator.AuthenticatorBase.invoke(AuthenticatorBase.java:606)
	at org.apache.catalina.core.StandardPipeline.doInvoke(StandardPipeline.java:702)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:673)
	at com.sun.enterprise.web.WebPipeline.invoke(WebPipeline.java:99)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:174)
	at org.apache.catalina.connector.CoyoteAdapter.doService(CoyoteAdapter.java:416)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:283)
	at com.sun.enterprise.v3.services.impl.ContainerMapper$HttpHandlerCallable.call(ContainerMapper.java:459)
	at com.sun.enterprise.v3.services.impl.ContainerMapper.service(ContainerMapper.java:167)
	at org.glassfish.grizzly.http.server.HttpHandler.runService(HttpHandler.java:206)
	at org.glassfish.grizzly.http.server.HttpHandler.doHandle(HttpHandler.java:180)
	at org.glassfish.grizzly.http.server.HttpServerFilter.handleRead(HttpServerFilter.java:235)
	at org.glassfish.grizzly.filterchain.ExecutorResolver$9.execute(ExecutorResolver.java:119)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeFilter(DefaultFilterChain.java:283)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeChainPart(DefaultFilterChain.java:200)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.execute(DefaultFilterChain.java:132)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.process(DefaultFilterChain.java:111)
	at org.glassfish.grizzly.ProcessorExecutor.execute(ProcessorExecutor.java:77)
	at org.glassfish.grizzly.nio.transport.TCPNIOTransport.fireIOEvent(TCPNIOTransport.java:536)
	at org.glassfish.grizzly.strategies.AbstractIOStrategy.fireIOEvent(AbstractIOStrategy.java:112)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy.run0(WorkerThreadIOStrategy.java:117)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy.access$100(WorkerThreadIOStrategy.java:56)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy$WorkerThreadRunnable.run(WorkerThreadIOStrategy.java:137)
	at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.doWork(AbstractThreadPool.java:591)
	at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.run(AbstractThreadPool.java:571)
	at java.lang.Thread.run(Thread.java:745)
]]

[2017-06-27T18:57:06.607+0200] [glassfish 4.1] [WARN] [] [org.jboss.weld.Servlet] [tid: _ThreadID=98 _ThreadName=http-listener-2(1)] [timeMillis: 1498582626607] [levelValue: 900] [[
  WELD-000714: HttpContextLifecycle guard leak detected. The Servlet container is not fully compliant. The value was 1]]

[2017-06-27T18:57:06.608+0200] [glassfish 4.1] [WARN] [] [org.jboss.weld.Context] [tid: _ThreadID=98 _ThreadName=http-listener-2(1)] [timeMillis: 1498582626608] [levelValue: 900] [[
  WELD-000225: Bean store leak was detected during org.jboss.weld.context.http.HttpRequestContextImpl association: org.apache.catalina.connector.RequestFacade@2ef273]]

[2017-06-27T18:57:06.609+0200] [glassfish 4.1] [WARN] [] [org.jboss.weld.Context] [tid: _ThreadID=98 _ThreadName=http-listener-2(1)] [timeMillis: 1498582626609] [levelValue: 900] [[
  WELD-000225: Bean store leak was detected during org.jboss.weld.context.http.HttpSessionContextImpl association: org.apache.catalina.connector.RequestFacade@2ef273]]

[2017-06-27T18:57:06.609+0200] [glassfish 4.1] [WARN] [] [org.jboss.weld.Conversation] [tid: _ThreadID=98 _ThreadName=http-listener-2(1)] [timeMillis: 1498582626609] [levelValue: 900] [[
  WELD-000335: Conversation context is already active, most likely it was not cleaned up properly during previous request processing: org.apache.catalina.connector.RequestFacade@2ef273]]



