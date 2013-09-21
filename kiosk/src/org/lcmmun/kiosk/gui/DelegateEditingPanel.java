package org.lcmmun.kiosk.gui;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.lcmmun.kiosk.Committee;
import org.lcmmun.kiosk.Delegate;
import org.lcmmun.kiosk.DelegateIcon;
import org.lcmmun.kiosk.IconType;
import org.lcmmun.kiosk.MemberStatus;
import org.lcmmun.kiosk.Messages;
import org.lcmmun.kiosk.resources.flags.FileNameGuesser;

import tools.customizable.ColorProperty;
import tools.customizable.FileProperty;
import tools.customizable.MultipleChoiceProperty;
import tools.customizable.PropertyPanel;
import tools.customizable.PropertySet;
import tools.customizable.TextProperty;

/**
 * A panel for editing a single delegate.
 * <p>
 * Not to be confused with {@link DelegatesConfigurationPanel}, which allows the
 * setup of many delegates.
 * 
 * @author William Chargin
 * 
 */
/**
 * @author William
 * 
 */
public class DelegateEditingPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The property containing the delegate's name.
	 */
	private final TextProperty tpName;

	/**
	 * The property containing the delegate's {@link MemberStatus}.
	 */
	private final MultipleChoiceProperty<MemberStatus> mcpStatus;

	/**
	 * The property containing the delegate's affiliation.
	 */
	private final MultipleChoiceProperty<String> mcpAffiliation;

	/**
	 * The delegate being edited.
	 */
	private final Delegate delegate;

	/**
	 * The property indicating the icon type of the delegate.
	 */
	private MultipleChoiceProperty<IconType> mcpIconType;

	/**
	 * The property containing the delegate's flag code.
	 */
	private final MultipleChoiceProperty<String> mcpFlag;

	/**
	 * The property containing the delegate's icon color.
	 */
	private final ColorProperty cpColor;

	/**
	 * The property containing the delegate's icon image.
	 */
	private final FileProperty fpImage;

	/**
	 * The list of invalid names.
	 */
	private final Collection<String> invalidNames;

	/**
	 * Creates a panel to edit the given delegate.
	 * 
	 * @param d
	 *            the delegate to edit
	 * @param c
	 *            the committee of which the delegate is a member
	 * @param invalidNames
	 *            the list of names already used
	 */
	public DelegateEditingPanel(Delegate d, Committee c,
			Collection<String> invalidNames) {
		super(new BorderLayout());

		delegate = d;
		this.invalidNames = invalidNames;

		PropertySet propertySet = new PropertySet();

		String name = d.getName();
		tpName = new TextProperty(Messages.getString("DelegateEditingPanel.PropertyName"), name == null ? new String() : name); //$NON-NLS-1$
		propertySet.add(tpName);

		mcpStatus = new MultipleChoiceProperty<MemberStatus>(Messages.getString("DelegateEditingPanel.PropertyMemberStatus"), //$NON-NLS-1$
				Arrays.asList(MemberStatus.values()), delegate.getStatus());
		propertySet.add(mcpStatus);

		final List<String> affiliations = c.getAffiliations();
		mcpAffiliation = new MultipleChoiceProperty<String>(Messages.getString("DelegateEditingPanel.PropertyAffiliation"), //$NON-NLS-1$
				affiliations, delegate.getAffiliation() == null ? new String()
						: delegate.getAffiliation());
		propertySet.add(mcpAffiliation);
		mcpAffiliation.setEnabled(!affiliations.isEmpty());

		propertySet.add(null);

		mcpIconType = MultipleChoiceProperty.createFromEnum(Messages.getString("DelegateEditingPanel.PropertyIconType"), //$NON-NLS-1$
				IconType.class);
		mcpIconType.setValue(delegate.getDelegateIcon().getType());
		propertySet.add(mcpIconType);
		mcpIconType.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent ce) {
				mcpFlag.setEnabled(mcpIconType.getValue().equals(IconType.FLAG));
				cpColor.setEnabled(mcpIconType.getValue()
						.equals(IconType.COLOR));
				fpImage.setEnabled(mcpIconType.getValue()
						.equals(IconType.IMAGE));
			}
		});

		mcpFlag = new MultipleChoiceProperty<String>(Messages.getString("DelegateEditingPanel.PropertyFlag"), //$NON-NLS-1$
				FileNameGuesser.imageNames.keySet(), delegate.getDelegateIcon()
						.getFlag());
		propertySet.add(mcpFlag);

		cpColor = new ColorProperty(Messages.getString("DelegateEditingPanel.PropertyColor"), delegate.getDelegateIcon() //$NON-NLS-1$
				.getColor());
		propertySet.add(cpColor);

		fpImage = new FileProperty(Messages.getString("DelegateEditingPanel.PropertyImageFileLocation"), delegate //$NON-NLS-1$
				.getDelegateIcon().getPathToImage());
		propertySet.add(fpImage);
		fpImage.setFilter(new FileNameExtensionFilter(Messages.getString("DelegateEditingPanel.FileFilterName"), "png", //$NON-NLS-1$ //$NON-NLS-2$
				"jpg", "gif")); //$NON-NLS-1$ //$NON-NLS-2$

		mcpFlag.setEnabled(!mcpIconType.getValue().equals(IconType.FLAG));
		cpColor.setEnabled(!mcpIconType.getValue().equals(IconType.COLOR));
		fpImage.setEnabled(!mcpIconType.getValue().equals(IconType.IMAGE));

		add(new PropertyPanel(propertySet, true, false), BorderLayout.CENTER);
	}

	/**
	 * Applies the changes elected by the user.
	 */
	public void applyChanges() {
		if (!invalidNames.contains(tpName.getValue())) {
			// The name is valid.
			delegate.setName(tpName.getValue());
		}

		final DelegateIcon di = delegate.getDelegateIcon();
		di.setFlag(mcpFlag.getValue());
		di.setColor(cpColor.getValue());
		di.setPathToImage(fpImage.getValue());
		di.setType(mcpIconType.getValue());

		delegate.setAffiliation(mcpAffiliation.getValue());
		delegate.setStatus(mcpStatus.getValue());
	}
}
