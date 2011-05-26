package de.codepitbull.objectsize.aspectj;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PageSizeResult implements Serializable{
	public Class<?> pageClass;
	public Long sizeBeforeDetach;
	public Long sizeAfterDetach;
}
