import com.untamedears.JukeAlert.model.Snitch;
import com.untamedears.citadel.entity.Faction;
import org.bukkit.Location;
import org.junit.Test;
import org.mockito.Mockito;

public class SnitchTest {

    @Test
    public void snitch_starts_with_empty_name(){
        Location location = Mockito.mock(Location.class);
        Faction group = Mockito.mock(Faction.class);

        Snitch snitch = new Snitch(location, group);
        assert snitch.getName().equals("");
    }

}