package ai.certifai;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * Integration test
 */
@Slf4j
public class AppTest 
{
    @Test
    public void initializeAppSuccessfully()
    {
        ai.certifai.IntellibelApp.main(null);
    }
}