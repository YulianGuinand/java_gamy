package gamy.services;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RawgApiServiceTest {

    private final RawgApiService rawgApiService = new RawgApiService();

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
        }
    }

    @Test
    void testSearchGamesAsync() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final boolean[] testPassed = {false};

        rawgApiService.searchGamesAsync("The Witcher", 
            games -> {
                assertNotNull(games, "La liste des jeux ne doit pas etre null.");
                System.out.println("Jeux trouves : " + games);
                
                testPassed[0] = !games.isEmpty();
                latch.countDown(); 
            },
            error -> {
                fail("L'appel API a echoue avec l'erreur : " + error.getMessage());
                latch.countDown();
            }
        );

        boolean finishedInTime = latch.await(10, TimeUnit.SECONDS);
        
        assertTrue(finishedInTime, "Le test a expire : l'API RAWG n'a pas repondu a temps.");
        assertTrue(testPassed[0], "La liste des jeux retournee par l'API est vide.");
    }
}