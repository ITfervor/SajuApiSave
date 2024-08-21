import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubstationInfoRepository extends JpaRepository<JSONObject, String> {
}
