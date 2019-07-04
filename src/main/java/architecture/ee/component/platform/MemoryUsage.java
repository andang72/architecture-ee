package architecture.ee.component.platform;

public class MemoryUsage {

	private static final long MEGABYTE = 0x100000L;

	private final Bytes maxHeap;
	private final Bytes freeAllocatedHeap;
	private final Bytes allocatedHeap;
	private final Bytes maxPermGen;
	private final Bytes usedPermGen;

	public MemoryUsage(Long maxHeap, Long freeAllocatedHeap, Long allocatedHeap, Long maxPermGen, Long usedPermGen) {
		this.maxHeap = new Bytes(maxHeap);
		this.freeAllocatedHeap = new Bytes(freeAllocatedHeap);
		this.allocatedHeap = new Bytes(allocatedHeap);
		this.maxPermGen = new Bytes(maxPermGen);
		this.usedPermGen = new Bytes(usedPermGen);
	}

	public MemoryUsage(Bytes maxHeap, Bytes freeAllocatedHeap, Bytes allocatedHeap, Bytes maxPermGen,
			Bytes usedPermGen) {
		this.maxHeap = maxHeap;
		this.freeAllocatedHeap = freeAllocatedHeap;
		this.allocatedHeap = allocatedHeap;
		this.maxPermGen = maxPermGen;
		this.usedPermGen = usedPermGen;
	}

	public Bytes getAvailableHeap() {
		return maxHeap.minus(allocatedHeap).plus(freeAllocatedHeap);
	}

	public Bytes getFreeAllocatedHeap() {
		return freeAllocatedHeap;
	}

	public Bytes getMaxHeap() {
		return maxHeap;
	}

	public Bytes getAllocateHeap() {
		return allocatedHeap;
	}

	public Bytes getUsedHeap() {
		return getAllocateHeap().minus(getFreeAllocatedHeap());
	}

	public Bytes getMaxPermGen() {
		return maxPermGen;
	}

	public Bytes getUsedPermGen() {
		return usedPermGen;
	}

	public Bytes getAvailablePermGen() {
		return maxPermGen.minus(usedPermGen);
	}

	public static class Bytes {

		private final long value;

		public Bytes(long value) {
			this.value = value;
		}

		public long bytes() {
			return value;
		}

		public long getBytes() {
			return bytes();
		}

		public long getMegabytes() {
			return megabytes();
		}

		public long megabytes() {
			return value / MEGABYTE;
		}

		public Bytes plus(Bytes b) {
			return new Bytes(value + b.bytes());
		}

		public Bytes minus(Bytes b) {
			return new Bytes(value - b.bytes());
		}

		public String toString() {
			return Long.toString(value);
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null || getClass() != obj.getClass())
				return false;
			Bytes bytes = (Bytes) obj;
			return value == bytes.value;
		}

		public int hashCode() {
			return (int) (value ^ value >>> 32);
		}
	}
}
