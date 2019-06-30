package emad.athena;

import org.junit.Test;

import emad.athena.Fragments.HomeFragment;
import emad.athena.Model.Recent;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void getNlpResponse_isCorrect()throws Exception{
        HomeFragment home = new HomeFragment();
        String response = home.getNlpAnswerTest("hi", null, true);
        assertEquals("Wrong","Hi,There",response);

    }

    @Test
    public void postQuestion(){
        HomeFragment homeFragment = new HomeFragment();
        int result = homeFragment.SendQuetionToFirebase(new Recent("Hello","Hello Dear", "22-6",true));
        assertEquals("wrong", 1, result,0.0);
    }
}