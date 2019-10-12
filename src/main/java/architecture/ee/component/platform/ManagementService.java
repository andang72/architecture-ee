package architecture.ee.component.platform;

import java.util.List;

public interface ManagementService {

	public abstract SystemInfo getSystemInfo();

	public abstract MemoryUsage getMemoryUsage();

	public abstract List<DiskUsage> getDiskUsages();

}
