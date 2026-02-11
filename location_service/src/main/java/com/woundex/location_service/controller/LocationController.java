
import com.woundex.location_service.infrastructure.redis.RedisGeoService;
import com.woundex.location_service.infrastructure.redis.RedisGeoService.NearbyDriver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class LocationController {

    private final RedisGeoService geo;

    public LocationController(RedisGeoService geo) { this.geo = geo; }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearbyDriver>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "1000") double radiusMeters,
            @RequestParam(defaultValue = "50") int limit) {

        List<NearbyDriver> drivers = geo.findNearby(new Position(lat, lon), radiusMeters, limit);
        return ResponseEntity.ok(drivers);
    }
}