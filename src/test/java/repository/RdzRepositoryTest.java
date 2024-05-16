package repository;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.acme.model.app_sms_833.Rdz;
import org.acme.repo.app_sms_833.RdzRepo;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
@QuarkusTest
public class RdzRepositoryTest {
    /*
    @Inject
    RdzRepo rdzRepo;
    /*
    @Test
    void listAll(){
        List<Rdz> rdzList=rdzRepo.listAll();
        assertFalse(rdzList.isEmpty());
    }

     */
}
