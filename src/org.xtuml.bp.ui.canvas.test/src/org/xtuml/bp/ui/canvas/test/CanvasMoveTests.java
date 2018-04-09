//=====================================================================
// Licensed under the Apache License, Version 2.0 (the "License"); you may not 
// use this file except in compliance with the License.  You may obtain a copy 
// of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   See the 
// License for the specific language governing permissions and limitations under
// the License.
//=====================================================================
package org.xtuml.bp.ui.canvas.test;

import org.eclipse.core.resources.IFileState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xtuml.bp.core.Component_c;
import org.xtuml.bp.core.CorePlugin;
import org.xtuml.bp.core.InterfaceReference_c;
import org.xtuml.bp.core.Package_c;
import org.xtuml.bp.core.PackageableElement_c;
import org.xtuml.bp.core.Port_c;
import org.xtuml.bp.core.Provision_c;
import org.xtuml.bp.core.common.ClassQueryInterface_c;
import org.xtuml.bp.core.ui.Selection;
import org.xtuml.bp.test.common.OrderedRunner;
import org.xtuml.bp.test.common.UITestingUtilities;
import org.xtuml.bp.ui.graphics.editor.GraphicalEditor;
import org.xtuml.bp.utilities.ui.CanvasUtilities;

@RunWith(OrderedRunner.class)
public class CanvasMoveTests extends CanvasTest {

	private String testModelName = "ModelElementMoveTests1";
	private static boolean initialized;
	private String test_id;
	public static boolean generateResults = false;
	private Package_c sourcePkg = null;
	private Package_c sourceFailPkg = null;
//	private Package_c sourceCompPass1 = null;
//	private Package_c sourceCompPass2 = null;
//	private Package_c sourceCompPass3 = null;
//	private Package_c sourceCompPass4 = null;
	private Package_c destPkg = null;
//	private Package_c destCompPkg = null;

	public CanvasMoveTests() {
		super(null, null);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		if(!initialized) {
			CorePlugin.disableParseAllOnResourceChange();
			loadProject(testModelName);
			initialized = true;
			Package_c pkgs[] = Package_c.getManyEP_PKGsOnR1405(m_sys);
			for (int i = 0; i < pkgs.length; i++) {
				if (pkgs[i].getName().equals("Source")) {
					sourcePkg = pkgs[i];
				}
				else if (pkgs[i].getName().equals("Destination")) {
					destPkg = pkgs[i];
				}
			}
		}
	}

	@Test
	public void testComponentWithInterfaceCutEnabledDisabled() {
		// MEM test section 7.0
		// Part 1:  We will verify the cut is NOT available on the element
		PackageableElement_c[] pes = PackageableElement_c.getManyPE_PEsOnR8000(sourcePkg);
		Component_c comp1 = null;
		for (int i = 0; i < pes.length; i++) {
			comp1 = Component_c.getOneC_COnR8001(pes[i], new ClassQueryInterface_c() {
				public boolean evaluate(Object candidate) {
					return ((Component_c)candidate).getName().equals("ComponentMovePass1");
				}
			});
		};
		CanvasUtilities.openCanvasEditor(sourcePkg);
		GraphicalEditor ce = (GraphicalEditor) UITestingUtilities.getActiveEditor();
		try {
			sourcePkg.getPersistableComponent().persist();
			IFileState[] history = sourcePkg.getPersistableComponent().getFile().getHistory(new NullProgressMonitor());
			sourcePkg.getPersistableComponent().getFile().setContents(history[0], 0, new NullProgressMonitor());
		} catch (CoreException e) {
			fail("Unable to touch test file.");
		}
		while(PlatformUI.getWorkbench().getDisplay().readAndDispatch());
		Selection sel = Selection.getInstance();
		sel.clear();
		sel.addToSelection(comp1);
		assertTrue("Cut was available.", UITestingUtilities.checkItemStatusInContextMenu(ce.getCanvas().getMenu(), "Cut", "", true));
		
		// Part 2: Now verify that cut is available on the element under the 
		//         package that was create in the previous test.
		// NOTE: Adding Provision is how you add attached interfaces.
		Provision_c pro = Provision_c.getOneC_POnR4009(InterfaceReference_c
				.getManyC_IRsOnR4016(Port_c.getManyC_POsOnR4010(comp1)));
		sel.addToSelection(pro);
		assertTrue("Cut was not available.", UITestingUtilities.checkItemStatusInContextMenu(ce.getCanvas().getMenu(), "Cut", "", false));
	}

	protected String getResultName() {
		return "MoveTests" + "_"  + test_id;
	}
}
