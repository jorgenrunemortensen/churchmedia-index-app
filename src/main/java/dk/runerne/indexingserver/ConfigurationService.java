package dk.runerne.indexingserver;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Hold and provides the system configuration. Configuration values are available by calling the various getter-methods.
 */
@Service
public class ConfigurationService {

}