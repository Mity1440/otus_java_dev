package ru.otus.cachehw;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.base.AbstractHibernateTest;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DbServiceClientWithCasheImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MyCacheTest extends AbstractHibernateTest {

    private static final Logger log = LoggerFactory.getLogger(MyCacheTest.class);
    private final int COUNT_OF_CLIENTS = 10000;
    private final int COUNT_OF_ITERATIONS = 10000;

    @Test
    void testErasingCashOnPerfomance(){

        var cashe = new MyCache<String, String>();

        IntStream.range(1, COUNT_OF_ITERATIONS).forEach(o->{
            cashe.put("key" + o, "value" + o);
        });
        log.info("Before gc. {}", cashe.count());

        System.gc();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.gc();

        log.info("After gc. survived {}", cashe.count());
        assertTrue(cashe.count() < COUNT_OF_ITERATIONS, "Ожидали что кэш очиститься, но это не так");

    }

    @Test
    void compareSpeedOfReadFromDB(){

        // WITHOUT CACHE
        Long timeServiceWithoutCashe = getTimeOfReadWithDBService(dbServiceClient);
        // WITH CACHE
        Long timeServiceWithCashe = getTimeOfReadWithDBService(dbServiceClientWithCashe);

        log.info("Total. Service without cashe. {}",  timeServiceWithoutCashe);
        log.info("Total. Service with cashe. {}",  timeServiceWithCashe);

        assertTrue(timeServiceWithoutCashe > timeServiceWithCashe,
                    "we think that cash is work, but ....");

    }

    private long getTimeOfReadWithDBService(DBServiceClient service){

        List<Long> clientIds = new ArrayList<>();

        log.info("start.write.");
        long mark1 = System.currentTimeMillis();
        IntStream.range(1, COUNT_OF_CLIENTS).forEach(o->{
            var clientSaved = service.saveClient(new Client("name" + o));
            clientIds.add(clientSaved.getId());
        });
        log.info("stop.write. Writing time in ms {}", System.currentTimeMillis() - mark1);

        log.info("start.read");
        long mark2 = System.currentTimeMillis();
        clientIds.forEach(o->{
            service.getClient(o);
        });
        long result = System.currentTimeMillis() - mark2;
        log.info("stop.read");

//        2022-01-27_17:01:44.075 INFO  ru.otus.cachehw.MyCacheTest - Total. Service without cashe. 18065
//        2022-01-27_17:01:44.075 INFO  ru.otus.cachehw.MyCacheTest - Total. Service with cashe. 85

        return result;

    }

}