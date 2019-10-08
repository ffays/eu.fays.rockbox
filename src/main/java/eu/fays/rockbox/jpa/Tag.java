package eu.fays.rockbox.jpa;

public enum Tag {
	/** Fast Track */
	FAST_TRACK("Fast Track"),
	/** Bug Fix */
	BUG_FIX("Bug Fix"),
	/** Wont Do */
	WONT_DO("Wont Do"),
	/** Deferred */
	DEFERRED("Deferred"),
	/** Quick Win */
	QUICK_WIN("Quick Win"),
	/** Long Run */
	LONG_RUN("Long Run");
	
	private final String label;
	
	
	@Override
	public String toString() {
		return label;
	}


	private Tag(final String label) {
		this.label = label;
	}
}
