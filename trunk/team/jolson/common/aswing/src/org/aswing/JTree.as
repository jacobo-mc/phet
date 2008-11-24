﻿/*
 Copyright aswing.org, see the LICENCE.txt.
*/

import org.aswing.ASColor;
import org.aswing.ASFont;
import org.aswing.CellEditor;
import org.aswing.Component;
import org.aswing.Container;
import org.aswing.DefaultTextFieldCellEditor;
import org.aswing.event.CellEditorListener;
import org.aswing.event.TreeModelEvent;
import org.aswing.event.TreeModelListener;
import org.aswing.event.TreeSelectionEvent;
import org.aswing.geom.Dimension;
import org.aswing.geom.Point;
import org.aswing.geom.Rectangle;
import org.aswing.Insets;
import org.aswing.LayoutManager;
import org.aswing.plaf.TreeUI;
import org.aswing.tree.DefaultMutableTreeNode;
import org.aswing.tree.DefaultTreeCell;
import org.aswing.tree.DefaultTreeModel;
import org.aswing.tree.DefaultTreeSelectionModel;
import org.aswing.tree.EmptySelectionModel;
import org.aswing.tree.GeneralTreeCellFactoryUIResource;
import org.aswing.tree.TreeCellEditor;
import org.aswing.tree.TreeCellFactory;
import org.aswing.tree.TreeModel;
import org.aswing.tree.TreePath;
import org.aswing.tree.TreePathMap;
import org.aswing.tree.TreeSelectionModel;
import org.aswing.UIManager;
import org.aswing.util.Delegate;
import org.aswing.util.Stack;
import org.aswing.util.Vector;
import org.aswing.Viewportable;

/**
 * A control that displays a set of hierarchical data as an outline.
 * <a name="jtree_description">
 * A control that displays a set of hierarchical data as an outline.
 * You can find task-oriented documentation and examples of using trees in
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/tree.html">How to Use Trees</a>,
 * a section in <em>The Java Tutorial.</em>
 * <p>
 * A specific node in a tree can be identified either by a
 * <code>TreePath</code> (an object
 * that encapsulates a node and all of its ancestors), or by its
 * display row, where each row in the display area displays one node.
 * An <i>expanded</i> node is a non-leaf node (as identified by
 * <code>TreeModel.isLeaf(node)</code> returning false) that will displays
 * its children when all its ancestors are <i>expanded</i>.
 * A <i>collapsed</i>
 * node is one which hides them. A <i>hidden</i> node is one which is
 * under a collapsed ancestor. All of a <i>viewable</i> nodes parents
 * are expanded, but may or may not be displayed. A <i>displayed</i> node
 * is both viewable and in the display area, where it can be seen.
 * <p>
 * Note that <code>JListTree</code> does not allow duplicate nodes to exist as children under 
 * the same parent too -- each sibling must be a unique object.
 * <p>
 * The following <code>JTree</code> methods use "visible" to mean "displayed":
 * <ul>
 * <li><code>isRootVisible()</code>
 * <li><code>setRootVisible()</code>
 * <li><code>scrollPathToVisible()</code>
 * <li><code>scrollRowToVisible()</code>
 * <li><code>getVisibleRowCount()</code>
 * <li><code>setVisibleRowCount()</code>
 * </ul>
 * <p>
 * The next group of <code>JTree</code> methods use "visible" to mean
 * "viewable" (under an expanded parent):
 * <ul>
 * <li><code>isPathVisible()</code>
 * <li><code>makePathVisible()</code>
 * </ul>
 * <p>
 * If you are interested in knowing when the selection listen the
 * the <code>ON_SELECTION_CHANGED</code> event.
 * <p>
 * If you are interested in detecting either double-click events or when
 * a user clicks on a node, regardless of whether or not it was selected,
 * we recommend you do the following:
 * <pre>
 *     yourTree.addEventListener(ON_PRESS, __onPressed, this);
 *     ....
 *     
 *     public function __onPressed(tree:Source):Void {
 *         TreePath selPath = tree.getMousePointedPath();
 *         ....
 *     }
 * };
 * </pre>
 * <p>
 * To use <code>JTree</code> to display compound nodes
 * (for example, nodes containing both
 * a graphic icon and text), subclass {@link TreeCell} and {@link TreeCellFactory} and use 
 * {@link #setCellFactory} to tell the tree to use it. To edit such nodes,
 * subclass {@link TreeCellEditor} and use {@link #setCellEditor}.
 * <p>
 * For a big model tree, there's two way to speed up the performance.
 * <br>
 * 1. Make all model item implemented Identifiable.<br>
 * 2. call JTree.setFixedCellWidth(width:Number) to fix the cell width.
 * 
 * @see TreeModel
 * @see org.aswing.tree.Identifiable
 * @author iiley
 */
class org.aswing.JTree extends Container implements Viewportable, TreeModelListener, CellEditorListener{
	
    /** Selection can only contain one path at a time. */
    public static var SINGLE_TREE_SELECTION:Number = DefaultTreeSelectionModel.SINGLE_TREE_SELECTION;

    /** Selection can only be contiguous. This will only be enforced if
     * a RowMapper instance is provided. That is, if no RowMapper is set
     * this behaves the same as DISCONTIGUOUS_TREE_SELECTION. */
    public static var CONTIGUOUS_TREE_SELECTION:Number = DefaultTreeSelectionModel.CONTIGUOUS_TREE_SELECTION;

    /** Selection can contain any number of items that are not necessarily
     * contiguous. */
    public static var DISCONTIGUOUS_TREE_SELECTION:Number = DefaultTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION;


	/**
	 * When the viewport's state changed.
	 * View position changed, view changed, all related to scroll things changed.
	 *<br>
	 * onStateChanged(source:JTree)
	 */	
	public static var ON_STATE_CHANGED:String = "onStateChanged"; 	
	
	/**
	 * When a property changed.
	 *<br>
	 * onPropertyChanged(source:JTree, propertyName:String, oldValue, newValue)
	 */	
	public static var ON_PROPERTY_CHANGED:String = "onPropertyChanged"; 	
 	
    /**
     * When selection changed.<br>
     * onSelectionChanged(source:JTree, e:TreeSelectionEvent); 
     */
    public static var ON_SELECTION_CHANGED:String = "onSelectionChanged";  
    
    /**
     * When an item in the tree has been expanded.<br>
     * param path - a TreePath object identifying the newly expanded node<br>
     * onTreeExpanded(source:JTree, path:TreePath)
     */
    public static var ON_TREE_EXPANDED:String = "onTreeExpanded";
    
    /**
     * When an item in the tree has been collapsed.<br>
     * param path - a TreePath object identifying the newly expanded node<br>
     * onTreeCollapsed(source:JTree, path:TreePath)
     */
    public static var ON_TREE_COLLAPSED:String = "onTreeCollapsed";
    
    /**
     * Invoked whenever a node in the tree is about to be expanded.<br>
     * param path - a TreePath object identifying the newly collapsed node<br>
     * It can throw a <code>ExpandVetoException</code> to indicate that this 
     * action is veto.<br>
     * onTreeWillExpand(source:JTree, path:TreePath)
     * @see org.aswing.event.ExpandVetoException
     */
    public static var ON_TREE_WILL_EXPAND:String = "onTreeWillExpand";
    
    /**
     * Invoked whenever a node in the tree is about to be collapsed.<br>
     * param path - a TreePath object identifying the newly collapsed node<br>
     * It can throw a <code>ExpandVetoException</code> to indicate that this 
     * action is veto.<br>
     * onTreeWillCollapse(source:JTree, path:TreePath)
     * @see org.aswing.event.ExpandVetoException
     */
    public static var ON_TREE_WILL_COLLAPSE:String = "onTreeWillCollapse";
    
	/**
	 * When the current cell is edited.<br>
	 * onTreeCellEditingStarted( source : JTree, path:TreePath, cellValue )<br>
	 * path the current cell's path<br>
	 * cellValue the current cell value ( before edition )<br>
	 */	
	public static var ON_CELL_EDITING_STARTED : String = "onTreeCellEditingStarted";
	
	/**
	 * When the current cell edition is canceled.<br>
	 * onTreeCellEditingCanceled( source : JTree, path:TreePath )<br>
	 * path the current cell's path<br>
	 */	
	public static var ON_CELL_EDITING_CANCELED : String = "onTreeCellEditingCanceled";
	
	/**
	 * When the current cell edition is finished.<br>
	 * onTreeCellEditingStopped( source : JTree, path:TreePath, newValue, oldValue )<br>
	 * path the current cell's path<br>
	 * newValue the new cell value<br>
	 * oldValue the old cell value ( before edition )<br>
	 */	
	public static var ON_CELL_EDITING_STOPPED : String = "onTreeCellEditingStopped";    
    
    /**
     * The model that defines the tree displayed by this object.
     */
    private var treeModel:TreeModel;

    /**
     * Models the set of selected nodes in this tree.
     */
    private var selectionModel:TreeSelectionModel;

    /**
     * True if the root node is displayed, false if its children are
     * the highest visible nodes.
     */
    private var rootVisible:Boolean;

    /**
     * The cell used to draw nodes. If <code>null</code>, the UI uses a default
     * <code>cellRenderer</code>.
     */
    private var cellFactory:TreeCellFactory;

    /**
     * Height to use for each display row. If this is <= 0 the renderer 
     * determines the height for each row.
     */
    private var rowHeight:Number;
    private var rowHeightSet:Boolean;
    
    private var fixedCellWidth:Number;

	private var selectionForeground:ASColor;
	private var selectionBackground:ASColor;
	
    /**
     * Maps from <code>TreePath</code> to <code>Boolean</code>
     * indicating whether or not the
     * particular path is expanded. This ONLY indicates whether a 
     * given path is expanded, and NOT if it is visible or not. That
     * information must be determined by visiting all the parent
     * paths and seeing if they are visible.
     */
    private var expandedState:TreePathMap;

    /**
     * Creates a new event and passed it off the
     * <code>selectionListeners</code>.
     */
    private var selectionRedirector:Object;

    /**
     * Editor for the entries.  Default is <code>null</code>
     * (tree is not editable).
     */
    private var cellEditor:TreeCellEditor;

    /**
     * Is the tree editable? Default is false.
     */
    private var editable:Boolean;

    /**
     * Number of rows to make visible at one time. This value is used for
     * the <code>Scrollable</code> interface. It determines the preferred
     * size of the display area.
     */
    private var visibleRowCount:Number;

    /**
     * If true, when editing is to be stopped by way of selection changing,
     * data in tree changing or other means <code>stopCellEditing</code>
     * is invoked, and changes are saved. If false,
     * <code>cancelCellEditing</code> is invoked, and changes
     * are discarded. Default is false.
     */
    private var invokesStopCellEditing:Boolean;

    /**
     * If true, when a node is expanded, as many of the descendants are 
     * scrolled to be visible.
     */
    private var scrollsOnExpand:Boolean;
    private var scrollsOnExpandSet:Boolean;

    /**
     * Number of mouse clicks before a node is expanded.
     */
    private var toggleClickCount:Number;

    /**
     * Used when <code>setExpandedState</code> is invoked,
     * will be a <code>Stack</code> of <code>Stack</code>s.
     */
    private var expandedStack:Stack;

    /**
     * Lead selection path, may not be <code>null</code>.
     */
    private var leadPath:TreePath;

    /**
     * Anchor path.
     */
    private var anchorPath:TreePath;

    /**
     * True if paths in the selection should be expanded.
     */
    private var expandsSelectedPaths:Boolean;

    /**
     * This is set to true for the life of the <code>setUI</code> call.
     */
    private var settingUI:Boolean;

	private var viewPosition:Point;
	private var verticalUnitIncrement:Number;
	private var verticalBlockIncrement:Number;
	private var horizontalUnitIncrement:Number;
	private var horizontalBlockIncrement:Number;
	
    /**
     * Max number of stacks to keep around.
     */
    private static var TEMP_STACK_SIZE:Number = 11;
	
    //
    // Bound property names
    //
    /** Bound property name for <code>cellFactory</code>. */
    public static var CELL_FACTORY_PROPERTY:String = "cellFactory";
    /** Bound property name for <code>treeModel</code>. */
    public static var TREE_MODEL_PROPERTY:String = "model";
    /** Bound property name for <code>rootVisible</code>. */
    public static var ROOT_VISIBLE_PROPERTY:String = "rootVisible";
    /** Bound property name for <code>showsRootHandles</code>. */
    /** Bound property name for <code>rowHeight</code>. */
    public static var ROW_HEIGHT_PROPERTY:String = "rowHeight";
    /** Bound property name for <code>cellEditor</code>. */
    public static var CELL_EDITOR_PROPERTY:String = "cellEditor";
    /** Bound property name for <code>editable</code>. */
    public static var EDITABLE_PROPERTY:String = "editable";
    /** Bound property name for selectionModel. */
    public static var SELECTION_MODEL_PROPERTY:String = "selectionModel";
    /** Bound property name for <code>visibleRowCount</code>. */
    public static var VISIBLE_ROW_COUNT_PROPERTY:String = "visibleRowCount";
    /** Bound property name for <code>messagesStopCellEditing</code>. */
    public static var INVOKES_STOP_CELL_EDITING_PROPERTY:String = "invokesStopCellEditing";
    /** Bound property name for <code>scrollsOnExpand</code>. */
    public static var SCROLLS_ON_EXPAND_PROPERTY:String = "scrollsOnExpand";
    /** Bound property name for <code>toggleClickCount</code>. */
    public static var TOGGLE_CLICK_COUNT_PROPERTY:String = "toggleClickCount";
    /** Bound property name for <code>leadSelectionPath</code>. */
    public static var LEAD_SELECTION_PATH_PROPERTY:String = "leadSelectionPath";
    /** Bound property name for anchor selection path. */
    public static var ANCHOR_SELECTION_PATH_PROPERTY:String = "anchorSelectionPath";
    /** Bound property name for expands selected paths property */
    public static var EXPANDS_SELECTED_PATHS_PROPERTY:String = "expandsSelectedPaths";
    /** Bound property name for font property  */
    public static var FONT_PROPERTY:String = "font";

    /**
     * Creates and returns a sample <code>TreeModel</code>.
     * Used primarily for beanbuilders to show something interesting.
     *
     * @return the default <code>TreeModel</code>
     */
    private static function getDefaultTreeModel():TreeModel {
        var root:DefaultMutableTreeNode = new DefaultMutableTreeNode("JTree");
		var parent:DefaultMutableTreeNode;
	
		parent = new DefaultMutableTreeNode("colors");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("blue"));
		parent.append(new DefaultMutableTreeNode("violet"));
		parent.append(new DefaultMutableTreeNode("red"));
		parent.append(new DefaultMutableTreeNode("yellow"));
	
		parent = new DefaultMutableTreeNode("sports");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("basketball"));
		parent.append(new DefaultMutableTreeNode("soccer"));
		parent.append(new DefaultMutableTreeNode("football"));
		parent.append(new DefaultMutableTreeNode("hockey"));
			
		parent = new DefaultMutableTreeNode("food");
		root.append(parent);
		parent.append(new DefaultMutableTreeNode("hot dogs"));
		parent.append(new DefaultMutableTreeNode("pizza"));
		parent.append(new DefaultMutableTreeNode("ravioli"));
		parent.append(new DefaultMutableTreeNode("bananas"));
		root.append(parent);
        return new DefaultTreeModel(root);
    }

    /**
     * JTree(newModel:TreeModel) <br>;
     * JTree() a defaul model will be created<br>
     * Returns an instance of <code>JTree</code> which displays the root node 
     * -- the tree is created using the specified data model.
     *
     * @param newModel  (optional)the <code>TreeModel</code> to use as the data model. If miss 
     * 					it, a defaul model will be created.
     * @see DefaultTreeModel#asksAllowsChildren
     */
    public function JTree(newModel:TreeModel) {
        super();
        setName("JTree");
        if(newModel == undefined) newModel = getDefaultTreeModel();
        viewPosition = new Point();
		expandedStack = new Stack();
		toggleClickCount = 3;
		expandedState = new TreePathMap();
        rowHeight = 16;
        rowHeightSet = false;
        fixedCellWidth = -1;
        visibleRowCount = 16;
        rootVisible = true;
        editable = false;
        selectionRedirector = new Object();
        selectionRedirector[DefaultTreeSelectionModel.ON_SELECTION_CHANGED] = Delegate.create(this, __valueChangedTreeSelectionRedirector);
        setSelectionModel(new DefaultTreeSelectionModel());
        cellFactory = null;
		scrollsOnExpand = true;
		scrollsOnExpandSet = false;
        setOpaque(true);
		expandsSelectedPaths = false;
		setCellFactory(new GeneralTreeCellFactoryUIResource(DefaultTreeCell));
        setCellEditor(new DefaultTextFieldCellEditor());
        setModel(newModel);
        updateUI();
    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the <code>TreeUI</code> object that renders this component
     */
    public function getUI():TreeUI {
        return TreeUI(ui);
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the <code>TreeUI</code> L&F object
     * @see UIDefaults#getUI()
     */
    public function setUI(ui:TreeUI):Void {
        if (this.ui != ui) {
		    settingUI = true;
		    //TODO check if need add expand/collpase listener to first
			super.setUI(ui);
			settingUI = false;
        }
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed. 
     * Replaces the current UI object with the latest version from the 
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public function updateUI():Void {
        setUI(TreeUI(UIManager.getUI(this)));
        invalidate();
    }


    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "TreeUI"
     * @see org.aswing.Component#getUIClassID()
     * @see org.aswing.UIDefaults#getUI()
     */
    public function getUIClassID():String {
        return "TreeUI";
    }
	
	public function getDefaultBasicUIClass():Function{
    	return org.aswing.plaf.basic.BasicTreeUI;
    }

	/**
	 * You can not set layout to JTree, it's handled by TreeUI.
	 * @throws Error when set any layout.
	 */
	public function setLayout(layout:LayoutManager):Void{
		trace("You can not set layout to JTree, it's handled by TreeUI");
		throw new Error("You can not set layout to JTree, it's handled by TreeUI");
	}
	
    /**
     * Returns the current <code>TreeCellFactory</code>
     *  that is rendering each cell.
     *
     * @return the <code>TreeCellFactory</code> that is rendering each cell
     */
    public function getCellFactory():TreeCellFactory {
        return cellFactory;
    }

    /**
     * Sets the <code>TreeCellFactory</code> that will be used to
     * draw each cell.
     *
     * @param x  the <code>TreeCellFactory</code> that is to render each cell
     */
    public function setCellFactory(x:TreeCellFactory):Void {
        var oldValue:TreeCellFactory = cellFactory;
		if(cellFactory != x){
	        cellFactory = x;
	        firePropertyChange(CELL_FACTORY_PROPERTY, oldValue, cellFactory);
	        invalidate();
		}
    }
    
    public function setFont(f:ASFont):Void{
    	var old:ASFont = getFont();
    	if(old != f){
	    	super.setFont(f);
	    	firePropertyChange(FONT_PROPERTY, old, getFont());
    	}
    }

    /**
      * Determines whether the tree is editable. Fires a property
      * change event if the new setting is different from the existing
      * setting.
      *
      * @param flag  a boolean value, true if the tree is editable
      */
    public function setEditable(flag:Boolean):Void {
        var oldValue:Boolean = editable;
		if(editable != flag){
	        editable = flag;
	        firePropertyChange(EDITABLE_PROPERTY, oldValue, flag);
		}
    }

    /**
     * Returns true if the tree is editable.
     *
     * @return true if the tree is editable
     */
    public function isEditable():Boolean {
        return editable;
    }

    /**
     * Sets the cell editor.  A <code>null</code> value implies that the
     * tree cannot be edited.  If this represents a change in the
     * <code>cellEditor</code>, the <code>propertyChange</code>
     * method is invoked on all listeners.
     *
     * @param cellEditor the <code>TreeCellEditor</code> to use
     */
    public function setCellEditor(cellEditor:TreeCellEditor):Void {
        var oldEditor:TreeCellEditor = this.cellEditor;
		if(oldEditor != cellEditor){
			oldEditor.removeCellEditorListener(this);
	        this.cellEditor = cellEditor;
	        cellEditor.addCellEditorListener(this);
	        firePropertyChange(CELL_EDITOR_PROPERTY, oldEditor, cellEditor);
	        invalidate();
		}
    }

    /**
     * Returns the editor used to edit entries in the tree.
     *
     * @return the <code>TreeCellEditor</code> in use,
     *		or <code>null</code> if the tree cannot be edited
     */
    public function getCellEditor():TreeCellEditor {
        return cellEditor;
    }

    /**
     * Returns the <code>TreeModel</code> that is providing the data.
     *
     * @return the <code>TreeModel</code> that is providing the data
     */
    public function getModel():TreeModel {
        return treeModel;
    }

    /**
     * Sets the <code>TreeModel</code> that will provide the data.
     *
     * @param newModel the <code>TreeModel</code> that is to provide the data
     */
    public function setModel(newModel:TreeModel):Void {
    	if(treeModel == newModel){
    		return;
    	}
        clearSelection();

        var oldModel:TreeModel = treeModel;

		if(treeModel != null)
		    treeModel.removeTreeModelListener(this);

        treeModel = newModel;
		clearToggledPaths();
		if(treeModel != null) {
			treeModel.addTreeModelListener(this);
		    // Mark the root as expanded, if it isn't a leaf.
		    if(treeModel.getRoot() != null && !treeModel.isLeaf(treeModel.getRoot())) {
				expandedState.put(new TreePath([treeModel.getRoot()]), true);
	        }
		}
        firePropertyChange(TREE_MODEL_PROPERTY, oldModel, treeModel);
        invalidate();
    }

    /**
     * Returns true if the root node of the tree is displayed.
     *
     * @return true if the root node of the tree is displayed
     */
    public function isRootVisible():Boolean {
        return rootVisible;
    }

    /**
     * Determines whether or not the root node from
     * the <code>TreeModel</code> is visible.
     *
     * @param rootVisible true if the root node of the tree is to be displayed
     */
    public function setRootVisible(rootVisible:Boolean):Void {
        var oldValue:Boolean = this.rootVisible;
		if(oldValue != rootVisible){
	        this.rootVisible = rootVisible;
	        firePropertyChange(ROOT_VISIBLE_PROPERTY, oldValue, this.rootVisible);
		}
    }

    /**
     * Sets the height of each cell, in pixels.  If the specified value
     * is less than or equal to zero nothing will changed.(the current cell height will not change.)
     * @param rowHeight the height of each cell, in pixels
     */
    public function setRowHeight(rowHeight:Number):Void {
    	if(rowHeight <= 0 || rowHeight == undefined || this.rowHeight == rowHeight){
    		return;
    	}
        var oldValue:Number = this.rowHeight;

        this.rowHeight = rowHeight;
		rowHeightSet = true;
        firePropertyChange(ROW_HEIGHT_PROPERTY, oldValue, this.rowHeight);
        invalidate();
    }

    /**
     * Returns the height of each row. Default is 16.
     *
     */
    public function getRowHeight():Number{
        return rowHeight;
    }
    

    /**
     * Returns the fixed cell width value -- the value specified by setting
     * the <code>fixedCellWidth</code> property, rather than that calculated
     * from the cell elements.
     *
     * @return the fixed cell width
     * @see #setFixedCellWidth()
     * @see #getRowHeight()
     */
    public function getFixedCellWidth():Number {
        return fixedCellWidth;
    }

    /**
     * Sets the width of every cell in the list.  If <code>width</code> is -1,
     * cell widths are computed by applying <code>getPreferredSize</code>
     * to the <code>TreeCell</code> component for each list element.
     * <p>
     * The default value of this property is -1.<br>
     * If your tree model is very large or all of your tree item has same width, 
     * i recommend you set a fixed cell with it will speed up the performance.
     * <p>
     *
     * @param width   the width, in pixels, for all cells in this list
     * @see #setFixedCellWidth()
     * @see #setRowHeight()
     */
    public function setFixedCellWidth(width:Number):Void {
    	if(width != fixedCellWidth){
	        fixedCellWidth = width;
	        treeDidChange();
    	}
    }    
    
    /**
     * Returns the foreground color for selected cells.
     *
     * @return the <code>Color</code> object for the foreground property
     * @see #setSelectionForeground()
     * @see #setSelectionBackground()
     */
	public function getSelectionForeground():ASColor{
		return selectionForeground;
	}
	
    /**
     * Sets the foreground color for selected cells.  Cell renderers
     * can use this color to render text and graphics for selected
     * cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * 
     * @param selectionForeground  the <code>Color</code> to use in the foreground
     *                             for selected list items
     * @see #getSelectionForeground()
     * @see #setSelectionBackground()
     * @see #setForeground()
     * @see #setBackground()
     * @see #setFont()
     */	
	public function setSelectionForeground(selectionForeground:ASColor):Void{
		var old:ASColor = this.selectionForeground;
		this.selectionForeground = selectionForeground;
		if (! selectionForeground.equals(old)){
			repaint();
			revalidate();
		}
	}
	
    /**
     * Returns the background color for selected cells.
     *
     * @return the <code>Color</code> used for the background of selected list items
     * @see #setSelectionBackground()
     * @see #setSelectionForeground()
     */	
	public function getSelectionBackground():ASColor{
		return selectionBackground;
	}
	
    /**
     * Sets the background color for selected cells.  Cell renderers
     * can use this color to the fill selected cells.
     * <p>
     * The default value of this property is defined by the look
     * and feel implementation.
     * @param selectionBackground  the <code>Color</code> to use for the background
     *                             of selected cells
     * @see #getSelectionBackground()
     * @see #setSelectionForeground()
     * @see #setForeground()
     * @see #setBackground()
     * @see #setFont()
     */	
	public function setSelectionBackground(selectionBackground:ASColor):Void{
		var old:ASColor = this.selectionBackground;
		this.selectionBackground = selectionBackground;
		if (! selectionBackground.equals(old)){
			repaint();
			revalidate();
		}
	}    

    /**
     * Determines what happens when editing is interrupted by selecting
     * another node in the tree, a change in the tree's data, or by some
     * other means. Setting this property to <code>true</code> causes the
     * changes to be automatically saved when editing is interrupted.
     * <p>
     * Fires a property change for the INVOKES_STOP_CELL_EDITING_PROPERTY.
     *
     * @param newValue true means that <code>stopCellEditing</code> is invoked 
     *        when editing is interrupted, and data is saved; false means that
     *        <code>cancelCellEditing</code> is invoked, and changes are lost
     */
    public function setInvokesStopCellEditing(newValue:Boolean):Void {
        var oldValue:Boolean = invokesStopCellEditing;
		if(oldValue != newValue){
	        invokesStopCellEditing = newValue;
	        firePropertyChange(INVOKES_STOP_CELL_EDITING_PROPERTY, oldValue, newValue);
		}
    }

    /**
     * Returns the indicator that tells what happens when editing is 
     * interrupted.
     *
     * @return the indicator that tells what happens when editing is 
     *         interrupted
     * @see #setInvokesStopCellEditing()
     */
    public function isInvokesStopCellEditing():Boolean {
        return invokesStopCellEditing;
    }

    /**
     * Sets the <code>scrollsOnExpand</code> property,
     * which determines whether the 
     * tree might scroll to show previously hidden children.
     * If this property is <code>true</code> (the default), 
     * when a node expands
     * the tree can use scrolling to make
     * the maximum possible number of the node's descendants visible.
     * In some look and feels, trees might not need to scroll when expanded;
     * those look and feels will ignore this property.
     *
     * @param newValue <code>false</code> to disable scrolling on expansion;
     *                 <code>true</code> to enable it
     * @see #getScrollsOnExpand()
     */
    public function setScrollsOnExpand(newValue:Boolean):Void {
		var oldValue:Boolean = scrollsOnExpand;
		if(oldValue != newValue){
			scrollsOnExpand = newValue;
			scrollsOnExpandSet = true;
	        firePropertyChange(SCROLLS_ON_EXPAND_PROPERTY, oldValue, newValue);
		}
    }

    /**
     * Returns the value of the <code>scrollsOnExpand</code> property.
     *
     * @return the value of the <code>scrollsOnExpand</code> property
     */
    public function isScrollsOnExpand():Boolean {
		return scrollsOnExpand;
    }

    /**
     * Sets the number of mouse clicks before a node will expand or close.
     * The default is 3. 
     */
    public function setToggleClickCount(clickCount:Number):Void {
		var oldCount:Number = toggleClickCount;
		if(oldCount != clickCount){
			toggleClickCount = clickCount;
			firePropertyChange(TOGGLE_CLICK_COUNT_PROPERTY, oldCount, clickCount);
		}
    }

    /**
     * Returns the number of mouse clicks needed to expand or close a node.
     *
     * @return number of mouse clicks before node is expanded
     */
    public function getToggleClickCount():Number {
		return toggleClickCount;
    }

    /**
     * Configures the <code>expandsSelectedPaths</code> property. If
     * true, any time the selection is changed, either via the
     * <code>TreeSelectionModel</code>, or the cover methods provided by 
     * <code>JTree</code>, the <code>TreePath</code>s parents will be
     * expanded to make them visible (visible meaning the parent path is
     * expanded, not necessarily in the visible rectangle of the
     * <code>JTree</code>). If false, when the selection
     * changes the nodes parent is not made visible (all its parents expanded).
     * This is useful if you wish to have your selection model maintain paths
     * that are not always visible (all parents expanded).
     *
     * @param newValue the new value for <code>expandsSelectedPaths</code>
     *               the parent of the path visible.
     */
    public function setExpandsSelectedPaths(newValue:Boolean):Void {
		var oldValue:Boolean = expandsSelectedPaths;
		if(oldValue != newValue){
			expandsSelectedPaths = newValue;
			firePropertyChange(EXPANDS_SELECTED_PATHS_PROPERTY, oldValue, newValue);
		}
    }

    /**
     * Returns the <code>expandsSelectedPaths</code> property.
     * @return true if selection changes result in the parent path being
     *         expanded
     * @see #setExpandsSelectedPaths()
     */
    public function isExpandsSelectedPaths():Boolean {
		return expandsSelectedPaths;
    }

    /**
     * Returns <code>isEditable</code>. This is invoked from the UI before
     * editing begins to insure that the given path can be edited. This
     * is provided as an entry point for subclassers to add filtered
     * editing without having to resort to creating a new editor.
     *
     * @return true if every parent node and the node itself is editable
     * @see #isEditable()
     */
    public function isPathEditable(path:TreePath):Boolean {
        return isEditable();
    }

    //
    // The following are convenience methods that get forwarded to the
    // current TreeUI.
    //

    /**
     * Returns the number of rows that are currently being displayed.
     *
     * @return the number of rows that are being displayed
     */
    public function getRowCount():Number {
        var tree:TreeUI = getUI();
        if(tree != null)
            return tree.getRowCount(this);
        return 0;
    }

    /** 
     * Selects the node identified by the specified path. If any
     * component of the path is hidden (under a collapsed node), and
     * <code>getExpandsSelectedPaths</code> is true it is 
     * exposed (made viewable).
     *
     * @param path the <code>TreePath</code> specifying the node to select
     */
    public function setSelectionPath(path:TreePath):Void {
        getSelectionModel().setSelectionPath(path);
    }

    /** 
     * Selects the nodes identified by the specified array of paths.
     * If any component in any of the paths is hidden (under a collapsed
     * node), and <code>getExpandsSelectedPaths</code> is true
     * it is exposed (made viewable).
     *
     * @param paths an array of <code>TreePath</code> objects that specifies
     *		the nodes to select
     */
    public function setSelectionPaths(paths:Array):Void {
        getSelectionModel().setSelectionPaths(paths);
    }

    /**
     * Sets the path identifies as the lead. The lead may not be selected.
     * The lead is not maintained by <code>JTree</code>,
     * rather the UI will update it.
     *
     * @param newPath  the new lead path
     */
    public function setLeadSelectionPath(newPath:TreePath):Void {
		var oldValue:TreePath = leadPath;
		if(!oldValue.equals(newPath)){
			leadPath = newPath;
			firePropertyChange(LEAD_SELECTION_PATH_PROPERTY, oldValue, newPath);
		}
    }

    /**
     * Sets the path identified as the anchor.
     * The anchor is not maintained by <code>JTree</code>, rather the UI will 
     * update it.
     *
     * @param newPath  the new anchor path
     */
    public function setAnchorSelectionPath(newPath:TreePath):Void {
		var oldValue:TreePath = anchorPath;
		if(!oldValue.equals(newPath)){
			anchorPath = newPath;
			firePropertyChange(ANCHOR_SELECTION_PATH_PROPERTY, oldValue, newPath);
		}
    }

    /**
     * Selects the node at the specified row in the display.
     *
     * @param row  the row to select, where 0 is the first row in
     *             the display
     */
    public function setSelectionRow(row:Number):Void {
        setSelectionRows([row]);
    }

    /**
     * Selects the nodes corresponding to each of the specified rows
     * in the display. If a particular element of <code>rows</code> is
     * < 0 or >= <code>getRowCount</code>, it will be ignored.
     * If none of the elements
     * in <code>rows</code> are valid rows, the selection will
     * be cleared. That is it will be as if <code>clearSelection</code>
     * was invoked.
     * 
     * @param rows  an array of ints specifying the rows to select,
     *              where 0 indicates the first row in the display
     */
    public function setSelectionRows( rows:Array):Void {
        var ui:TreeUI = getUI();

        if(ui != null && rows != null) {
            var numRows:Number = rows.length;
            var paths:Array = new Array(numRows);

            for(var counter:Number = 0; counter < numRows; counter++) {
                paths[counter] = ui.getPathForRow(this, rows[counter]);
	    	}
            setSelectionPaths(paths);
        }
    }

    /**
     * Adds the node identified by the specified <code>TreePath</code>
     * to the current selection. If any component of the path isn't
     * viewable, and <code>getExpandsSelectedPaths</code> is true it is 
     * made viewable.
     * <p>
     * Note that <code>JTree</code> does not allow duplicate nodes to
     * exist as children under the same parent -- each sibling must be
     * a unique object.
     *
     * @param path the <code>TreePath</code> to add
     */
    public function addSelectionPath(path:TreePath):Void {
        getSelectionModel().addSelectionPath(path);
    }

    /**
     * Adds each path in the array of paths to the current selection. If
     * any component of any of the paths isn't viewable and
     * <code>getExpandsSelectedPaths</code> is true, it is
     * made viewable.
     * <p>
     * Note that <code>JTree</code> does not allow duplicate nodes to
     * exist as children under the same parent -- each sibling must be
     * a unique object.
     *
     * @param paths an array of <code>TreePath</code> objects that specifies
     *		the nodes to add
     */
    public function addSelectionPaths(paths:Array):Void {
		getSelectionModel().addSelectionPaths(paths);
    }

    /**
     * Adds the path at the specified row to the current selection.
     *
     * @param row  an integer specifying the row of the node to add,
     *             where 0 is the first row in the display
     */
    public function addSelectionRow(row:Number):Void {
        addSelectionRows([row]);
    }

    /**
     * Adds the paths at each of the specified rows to the current selection.
     * 
     * @param rows  an array of ints specifying the rows to add,
     *              where 0 indicates the first row in the display
     */
    public function addSelectionRows(rows:Array):Void {
        var ui:TreeUI = getUI();

        if(ui != null && rows != null) {
            var numRows:Number = rows.length;
            var paths:Array = new Array(numRows);

            for(var counter:Number = 0; counter < numRows; counter++)
                paths[counter] = ui.getPathForRow(this, rows[counter]);
            addSelectionPaths(paths);
        }
    }

    /**
     * Returns the last path component in the first node of the current 
     * selection.
     *
     * @return the last <code>Object</code> in the first selected node's
     *		<code>TreePath</code>,
     *		or <code>null</code> if nothing is selected
     * @see TreePath#getLastPathComponent
     */
    public function getLastSelectedPathComponent():Object {
        var selPath:TreePath = getSelectionModel().getSelectionPath();

        if(selPath != null)
            return selPath.getLastPathComponent();
        return null;
    }

    /**
     * Returns the path identified as the lead.
     * @return path identified as the lead
     */
    public function getLeadSelectionPath():TreePath {
		return leadPath;
    }

    /**
     * Returns the path identified as the anchor.
     * @return path identified as the anchor
     */
    public function getAnchorSelectionPath():TreePath {
		return anchorPath;
    }

    /**
     * Returns the path to the first selected node.
     *
     * @return the <code>TreePath</code> for the first selected node,
     *		or <code>null</code> if nothing is currently selected
     */
    public function getSelectionPath():TreePath {
        return getSelectionModel().getSelectionPath();
    }

    /**
     * Returns the paths of all selected values.
     *
     * @return an array of <code>TreePath</code> objects indicating the selected
     *         nodes, or <code>null</code> if nothing is currently selected
     */
    public function getSelectionPaths():Array {
        return getSelectionModel().getSelectionPaths();
    }

    /**
     * Returns all of the currently selected rows. This method is simply
     * forwarded to the <code>TreeSelectionModel</code>.
     * If nothing is selected <code>null</code> or an empty array will
     * be returned, based on the <code>TreeSelectionModel</code>
     * implementation.
     *
     * @return an array of integers that identifies all currently selected rows
     *         where 0 is the first row in the display
     */
    public function getSelectionRows():Array {
        return getSelectionModel().getSelectionRows();
    }

    /**
     * Returns the number of nodes selected.
     *
     * @return the number of nodes selected
     */
    public function getSelectionCount():Number {
        return selectionModel.getSelectionCount();
    }

    /**
     * Gets the first selected row.
     *
     * @return an integer designating the first selected row, where 0 is the 
     *         first row in the display
     */
    public function getMinSelectionRow():Number {
        return getSelectionModel().getMinSelectionRow();
    }

    /**
     * Returns the last selected row.
     *
     * @return an integer designating the last selected row, where 0 is the 
     *         first row in the display
     */
    public function getMaxSelectionRow():Number {
        return getSelectionModel().getMaxSelectionRow();
    }

    /**
     * Returns the row index corresponding to the lead path.
     *
     * @return an integer giving the row index of the lead path,
     *		where 0 is the first row in the display; or -1
     *		if <code>leadPath</code> is <code>null</code>
     */
    public function getLeadSelectionRow():Number {
		var leadPath:TreePath = getLeadSelectionPath();
	
		if (leadPath != null) {
		    return getRowForPath(leadPath);
		}
        return -1;
    }

    /**
     * Returns true if the item identified by the path is currently selected.
     *
     * @param path a <code>TreePath</code> identifying a node
     * @return true if the node is selected
     */
    public function isPathSelected(path:TreePath):Boolean {
        return getSelectionModel().isPathSelected(path);
    }

    /**
     * Returns true if the node identified by row is selected.
     *
     * @param row  an integer specifying a display row, where 0 is the first
     *             row in the display
     * @return true if the node is selected
     */
    public function isRowSelected(row:Number):Boolean {
        return getSelectionModel().isRowSelected(row);
    }

    /**
     * Returns an <code>Array</code> of the descendants of the
     * path <code>parent</code> that
     * are currently expanded. If <code>parent</code> is not currently
     * expanded, this will return <code>null</code>.
     * If you expand/collapse nodes while
     * iterating over the returned <code>Array</code>
     * this may not return all
     * the expanded paths, or may return paths that are no longer expanded.
     *
     * @param parent  the path which is to be examined
     * @return an <code>Array</code> of the descendents of 
     *		<code>parent</code>, or <code>null</code> if
     *		<code>parent</code> is not currently expanded
     */
    public function getExpandedDescendants(parent:TreePath):Array {
		if(!isExpanded(parent)){
		    return null;
		}
	
		var toggledPaths:Array = expandedState.keys();
		var elements:Vector = null;
		var path:TreePath;
		var value:Boolean;
	
		if(toggledPaths != null) {
			for(var i:Number=0; i<toggledPaths.length; i++){
				path = TreePath(toggledPaths[i]);
				value = expandedState.get(path);
				// Add the path if it is expanded, a descendant of parent,
				// and it is visible (all parents expanded). This is rather
				// expensive!
				if(path != parent && value==true &&
				   parent.isDescendant(path) && isPathVisible(path)) {
				    if (elements == null) {
						elements = new Vector();
				    }
				    elements.append(path);
				}
		    }
		}
		if (elements == null) {
		    return [];
		}
		return elements.toArray();
    }

    /**
     * Returns true if the node identified by the path has ever been
     * expanded.
     * @return true if the <code>path</code> has ever been expanded
     */
    public function hasBeenExpanded(path:TreePath):Boolean {
		return (path != null && expandedState.get(path) != null);
    }

    /**
     * Returns true if the node identified by the path is currently expanded,
     * 
     * @param path  the <code>TreePath</code> specifying the node to check
     * @return false if any of the nodes in the node's path are collapsed, 
     *               true if all nodes in the path are expanded
     */
    public function isExpanded(path:TreePath):Boolean {
		if(path == null)
		    return false;

		// Is this node expanded?
		var value:Boolean = expandedState.get(path);

		if(value != true){
	    	return false;
		}

		// It is, make sure its parent is also expanded.
		var parentPath:TreePath = path.getParentPath();
	
		if(parentPath != null){
		    return isExpanded(parentPath);
		}
	    return true;
    }

    /**
     * Returns true if the node at the specified display row is currently
     * expanded.
     * 
     * @param row  the row to check, where 0 is the first row in the 
     *             display
     * @return true if the node is currently expanded, otherwise false
     */
    public function isExpandedOfRow(row:Number):Boolean {
        var tree:TreeUI = getUI();

        if(tree != null) {
	    	var path:TreePath = tree.getPathForRow(this, row);

	    	if(path != null) {
                var value:Boolean = expandedState.get(path);
                return (value == true);
            }
		}
        return false;
    }

    /**
     * Returns true if the value identified by path is currently collapsed,
     * this will return false if any of the values in path are currently
     * not being displayed.
     * 
     * @param path  the <code>TreePath</code> to check
     * @return true if any of the nodes in the node's path are collapsed, 
     *               false if all nodes in the path are expanded
     */
    public function isCollapsed(path:TreePath):Boolean {
		return !isExpanded(path);
    }

    /**
     * Returns true if the node at the specified display row is collapsed.
     * 
     * @param row  the row to check, where 0 is the first row in the 
     *             display
     * @return true if the node is currently collapsed, otherwise false
     */
    public function isCollapsedOfRow(row:Number):Boolean {
		return !isExpandedOfRow(row);
    }

    /**
     * Ensures that the node identified by path is currently viewable.
     *
     * @param path  the <code>TreePath</code> to make visible
     */
    public function makePathVisible(path:TreePath):Void {
        if(path != null) {
		    var parentPath:TreePath = path.getParentPath();
	
		    if(parentPath != null) {
				expandPath(parentPath);
		    }
        }
    }

    /**
     * Returns true if the value identified by path is currently viewable,
     * which means it is either the root or all of its parents are expanded.
     * Otherwise, this method returns false. 
     *
     * @return true if the node is viewable, otherwise false
     */
    public function isPathVisible(path:TreePath):Boolean {
        if(path != null) {
		    var parentPath:TreePath = path.getParentPath();
	
		    if(parentPath != null){
				return isExpanded(parentPath);
		    }
		    // Root.
		    return true;
		}
        return false;
    }

    /**
     * Returns the <code>Rectangle</code> that the specified node will be drawn
     * into. Returns <code>null</code> if any component in the path is hidden
     * (under a collapsed parent).
     * <p>
     * Note:<br>
     * This method returns a valid rectangle, even if the specified
     * node is not currently displayed.
     *
     * @param path the <code>TreePath</code> identifying the node
     * @return the <code>Rectangle</code> the node is drawn in,
     *		or <code>null</code> 
     */
    public function getPathBounds(path:TreePath):Rectangle {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getPathBounds(this, path);
        return null;
    }

    /**
     * Returns the <code>Rectangle</code> that the node at the specified row is
     * drawn in.
     *
     * @param row  the row to be drawn, where 0 is the first row in the 
     *             display
     * @return the <code>Rectangle</code> the node is drawn in 
     */
    public function getRowBounds(row:Number):Rectangle {
		return getPathBounds(getPathForRow(row));
    }
	/**
	 * Returns the location in the JTree view area of the logic location.
	 */
	public function getPixelLocationFromLogicLocation(p:Point):Point{
		var pp:Point = new Point(p);
		var startP:Point = getViewStartPoint();
		pp.move(startP.x, startP.y);
		return pp;
	}
	
	/**
	 * Returns the location in the JTree view area of the logic location.
	 */
	public function getLogicLocationFromPixelLocation(p:Point):Point{
		var pp:Point = new Point(p);
		var startP:Point = getViewStartPoint();
		pp.move(-startP.x, -startP.y);
		return pp;
	}
	
	private function getViewStartPoint():Point{
		var viewPos:Point = getViewPosition();
		var insets:Insets = getInsets();
		var insetsX:Number = insets.left;
		var insetsY:Number = insets.top;
		var startX:Number = insetsX - viewPos.x;
		var startY:Number = insetsY - viewPos.y;
		return new Point(startX, startY);
	}
	
    /**
     * Makes sure all the path components in path are expanded (except
     * for the last path component) and scrolls so that the 
     * node identified by the path is displayed. Only works when this
     * <code>JTree</code> is contained in a <code>JScrollPane</code>.
     * 
     * @param path  the <code>TreePath</code> identifying the node to
     * 		bring into view
     */
    public function scrollPathToVisible(path:TreePath):Void {
		if(path != null) {
		    makePathVisible(path);
	
		    var bounds:Rectangle = getPathBounds(path);
	
		    if(bounds != null) {
				scrollRectToVisible(bounds);
		    }
		}
    }

    /**
     * Scrolls the item identified by row until it is displayed. The minimum
     * of amount of scrolling necessary to bring the row into view
     * is performed. Only works when this <code>JTree</code> is contained in a
     * <code>JScrollPane</code>.
     *
     * @param row  an integer specifying the row to scroll, where 0 is the
     *             first row in the display
     */
    public function scrollRowToVisible(row:Number):Void {
		scrollPathToVisible(getPathForRow(row));
    }

    /**
     * Returns the treePath that the user mouse pointed, null if no path was pointed.
     * @return the path mouse pointed, null if not pointed any one.
     */
    public function getMousePointedPath():TreePath{
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getMousePointedPath();
        return null;
    }

    /**
     * Returns the path for the specified row.  If <code>row</code> is
     * not visible, <code>null</code> is returned.
     *
     * @param row  an integer specifying a row
     * @return the <code>TreePath</code> to the specified node,
     *		<code>null</code> if <code>row < 0</code>
     *		or <code>row > getRowCount()</code>
     */
    public function getPathForRow(row:Number):TreePath {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getPathForRow(this, row);
        return null;
    }

    /**
     * Returns the row that displays the node identified by the specified
     * path. 
     * 
     * @param path  the <code>TreePath</code> identifying a node
     * @return an integer specifying the display row, where 0 is the first
     *         row in the display, or -1 if any of the elements in path
     *         are hidden under a collapsed parent.
     */
    public function getRowForPath(path:TreePath):Number {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getRowForPath(this, path);
        return -1;
    }

    /**
     * Ensures that the node identified by the specified path is 
     * expanded and viewable. If the last item in the path is a
     * leaf, this will have no effect.
     * 
     * @param path  the <code>TreePath</code> identifying a node
     */
    public function expandPath(path:TreePath):Void {
		// Only expand if not leaf!
		var model:TreeModel = getModel();
	
		if(path != null && model != null && !model.isLeaf(path.getLastPathComponent())) {
		    setExpandedState(path, true);
		}
    }

    /**
     * Ensures that the node in the specified row is expanded and
     * viewable.
     * <p>
     * If <code>row</code> is < 0 or >= <code>getRowCount</code> this
     * will have no effect.
     *
     * @param row  an integer specifying a display row, where 0 is the
     *             first row in the display
     */
    public function expandRow(row:Number):Void {
		expandPath(getPathForRow(row));
    }

    /**
     * Ensures that the node identified by the specified path is 
     * collapsed and viewable.
     * 
     * @param path  the <code>TreePath</code> identifying a node
      */
    public function collapsePath(path:TreePath):Void {
		setExpandedState(path, false);
    }

    /**
     * Ensures that the node in the specified row is collapsed.
     * <p>
     * If <code>row</code> is < 0 or >= <code>getRowCount</code> this
     * will have no effect.
     *
     * @param row  an integer specifying a display row, where 0 is the
     *             first row in the display
      */
    public function collapseRow(row:Number):Void {
		collapsePath(getPathForRow(row));
    }

    /**
     * Returns the path for the node at the specified location.
     *
     * @param x an integer giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an integer giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return  the <code>TreePath</code> for the node at that location
     */
    public function getPathForLocation(x:Number, y:Number):TreePath {
        var closestPath:TreePath = getClosestPathForLocation(x, y);

        if(closestPath != null) {
            var pathBounds:Rectangle = getPathBounds(closestPath);

            if(pathBounds != null &&
               x >= pathBounds.x && x < (pathBounds.x + pathBounds.width) &&
               y >= pathBounds.y && y < (pathBounds.y + pathBounds.height))
                return closestPath;
        }
        return null;
    }

    /**
     * Returns the row for the specified location. 
     *
     * @param x an integer giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an integer giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return the row corresponding to the location, or -1 if the
     *         location is not within the bounds of a displayed cell
     * @see #getClosestRowForLocation
     */
    public function getRowForLocation(x:Number, y:Number):Number {
		return getRowForPath(getPathForLocation(x, y));
    }

    /**
     * Returns the path to the node that is closest to x,y.  If
     * no nodes are currently viewable, or there is no model, returns
     * <code>null</code>, otherwise it always returns a valid path.  To test if
     * the node is exactly at x, y, get the node's bounds and
     * test x, y against that.
     *
     * @param x an integer giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an integer giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return  the <code>TreePath</code> for the node closest to that location,
     *          <code>null</code> if nothing is viewable or there is no model
     *
     * @see #getPathForLocation
     * @see #getPathBounds
     */
    public function getClosestPathForLocation(x:Number, y:Number):TreePath {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getClosestPathForLocation(this, x, y);
        return null;
    }

    /**
     * Returns the row to the node that is closest to x,y.  If no nodes
     * are viewable or there is no model, returns -1. Otherwise,
     * it always returns a valid row.  To test if the returned object is 
     * exactly at x, y, get the bounds for the node at the returned
     * row and test x, y against that.
     *
     * @param x an integer giving the number of pixels horizontally from
     *          the left edge of the display area, minus any left margin
     * @param y an integer giving the number of pixels vertically from
     *          the top of the display area, minus any top margin
     * @return the row closest to the location, -1 if nothing is
     *         viewable or there is no model
     *
     * @see #getRowForLocation
     * @see #getRowBounds
     */
    public function getClosestRowForLocation(x:Number, y:Number):Number {
		return getRowForPath(getClosestPathForLocation(x, y));
    }

    /**
     * Returns true if the tree is being edited. The item that is being
     * edited can be obtained using <code>getSelectionPath</code>.
     *
     * @return true if the user is currently editing a node
     * @see #getSelectionPath
     */
    public function isEditing():Boolean {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.isEditing(this);
        return false;
    }

    /**
     * Ends the current editing session.
     * (The <code>DefaultTreeCellEditor</code> 
     * object saves any edits that are currently in progress on a cell.
     * Other implementations may operate differently.) 
     * Has no effect if the tree isn't being edited.
     * <blockquote>
     * <b>Note:</b><br>
     * To make edit-saves automatic whenever the user changes
     * their position in the tree, use {@link #setInvokesStopCellEditing}.
     * </blockquote>
     *
     * @return true if editing was in progress and is now stopped,
     *              false if editing was not in progress
     */
    public function stopEditing():Boolean {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.stopEditing(this);
        return false;
    }

    /**
     * Cancels the current editing session. Has no effect if the
     * tree isn't being edited.
     */
    public function  cancelEditing():Void {
        var tree:TreeUI = getUI();

        if(tree != null)
	    	tree.cancelEditing(this);
    }

    /**
     * Selects the node identified by the specified path and initiates
     * editing.  The edit-attempt fails if the <code>CellEditor</code>
     * does not allow
     * editing for the specified item.
     * 
     * @param path  the <code>TreePath</code> identifying a node
     */
    public function startEditingAtPath(path:TreePath):Void {
        var tree:TreeUI = getUI();

        if(tree != null){
            if(tree.startEditingAtPath(this, path)){
            	dispatchEvent(createEventObj( ON_CELL_EDITING_STARTED, path, path.getLastPathComponent() ));
            }
        }
    }

    /**
     * Returns the path to the element that is currently being edited.
     *
     * @return  the <code>TreePath</code> for the node being edited
     */
    public function getEditingPath():TreePath {
        var tree:TreeUI = getUI();

        if(tree != null)
            return tree.getEditingPath(this);
        return null;
    }

    //
    // Following are primarily convenience methods for mapping from
    // row based selections to path selections.  Sometimes it is
    // easier to deal with these than paths (mouse downs, key downs
    // usually just deal with index based selections).
    // Since row based selections require a UI many of these won't work
    // without one.
    //

    /**
     * Sets the tree's selection model. When a <code>null</code> value is
     * specified an emtpy
     * <code>selectionModel</code> is used, which does not allow selections.
     *
     * @param selectionModel the <code>TreeSelectionModel</code> to use,
     *		or <code>null</code> to disable selections
     * @see TreeSelectionModel
     */
    public function setSelectionModel(selectionModel:TreeSelectionModel):Void {
    	if(this.selectionModel == selectionModel){
    		return;
    	}
        if(selectionModel == null){
            selectionModel = EmptySelectionModel.sharedInstance();
        }
        var oldValue:TreeSelectionModel = this.selectionModel;

		if (this.selectionModel != null && selectionRedirector != null) {
	            this.selectionModel.removeEventListener(selectionRedirector);
		}

        this.selectionModel = selectionModel;
		if (selectionRedirector != null) {
	            this.selectionModel.addEventListener(selectionRedirector);
		}
        firePropertyChange(SELECTION_MODEL_PROPERTY, oldValue, this.selectionModel);
    }

    /**
     * Returns the model for selections. This should always return a 
     * non-<code>null</code> value. If you don't want to allow anything
     * to be selected
     * set the selection model to <code>null</code>, which forces an empty
     * selection model to be used.
     *
     * @see #setSelectionModel()
     */
    public function getSelectionModel():TreeSelectionModel {
        return selectionModel;
    }

	/**
	 * Proxy method which passes specified selection mode to the currently 
	 * configured selection model.
	 * @param mode the selection mode identifier
	 */
	public function setSelectionMode(mode:Number):Void {
		selectionModel.setSelectionMode(mode);	
	}

    /**
     * Returns <code>JTreePath</code> instances representing the path
     * between index0 and index1 (including index1).
     * Returns <code>null</code> if there is no tree.
     *
     * @param index0  an integer specifying a display row, where 0 is the
     *                first row in the display
     * @param index1  an integer specifying a second display row
     * @return an array of <code>TreePath</code> objects, one for each
     *		node between index0 and index1, inclusive; or <code>null</code>
     *		if there is no tree
     */
    private function getPathBetweenRows(index0:Number, index1:Number):Array {
        var newMinIndex:Number, newMaxIndex:Number;
        var tree:TreeUI = getUI();

        newMinIndex = Math.min(index0, index1);
        newMaxIndex = Math.max(index0, index1);

        if(tree != null) {
            var selection:Array = new Array(newMaxIndex - newMinIndex + 1);

            for(var counter:Number = newMinIndex; counter <= newMaxIndex; counter++){
                selection[counter - newMinIndex] = tree.getPathForRow(this, counter);
            }
            return selection;
        }
        return null;
    }

    /**
     * Selects the nodes between index0 and index1, inclusive.
     *
     * @param index0  an integer specifying a display row, where 0 is the
     *                first row in the display
     * @param index1  an integer specifying a second display row
    */
    public function setSelectionInterval(index0:Number, index1:Number):Void {
        var paths:Array = getPathBetweenRows(index0, index1);
        getSelectionModel().setSelectionPaths(paths);
    }

    /**
     * Adds the paths between index0 and index1, inclusive, to the 
     * selection.
     *
     * @param index0  an integer specifying a display row, where 0 is the
     *                first row in the display
     * @param index1  an integer specifying a second display row
     */
    public function addSelectionInterval(index0:Number, index1:Number):Void {
        var paths:Array = getPathBetweenRows(index0, index1);

        getSelectionModel().addSelectionPaths(paths);
    }

    /**
     * Removes the nodes between index0 and index1, inclusive, from the 
     * selection.
     *
     * @param index0  an integer specifying a display row, where 0 is the
     *                first row in the display
     * @param index1  an integer specifying a second display row
     */
    public function removeSelectionInterval(index0:Number, index1:Number):Void {
        var paths:Array = getPathBetweenRows(index0, index1);

        this.getSelectionModel().removeSelectionPaths(paths);
    }

    /**
     * Removes the node identified by the specified path from the current
     * selection.
     * 
     * @param path  the <code>TreePath</code> identifying a node
     */
    public function removeSelectionPath(path:TreePath):Void {
        getSelectionModel().removeSelectionPath(path);
    }

    /**
     * Removes the nodes identified by the specified paths from the 
     * current selection.
     *
     * @param paths an array of <code>TreePath</code> objects that
     *              specifies the nodes to remove
     */
    public function removeSelectionPaths(paths:Array):Void {
        getSelectionModel().removeSelectionPaths(paths);
    }

    /**
     * Removes the row at the index <code>row</code> from the current
     * selection.
     * 
     * @param row  the row to remove
     */
    public function removeSelectionRow(row:Number):Void {
        removeSelectionRows([row]);
    }

    /**
     * Removes the rows that are selected at each of the specified
     * rows.
     *
     * @param rows  an array of ints specifying display rows, where 0 is 
     *             the first row in the display
     */
    public function removeSelectionRows(rows:Array):Void {
        var ui:TreeUI = getUI();

        if(ui != null && rows != null) {
            var numRows:Number = rows.length;
            var paths:Array = new Array(numRows);

            for(var counter:Number = 0; counter < numRows; counter++){
                paths[counter] = ui.getPathForRow(this, rows[counter]);
            }
            removeSelectionPaths(paths);
        }
    }

    /**
     * Clears the selection.
     */
    public function clearSelection():Void {
        getSelectionModel().clearSelection();
    }

    /**
     * Returns true if the selection is currently empty.
     *
     * @return true if the selection is currently empty
     */
    public function isSelectionEmpty():Boolean {
        return getSelectionModel().isSelectionEmpty();
    }
    
    /**
     * Adds a listener for <code>TreeExpansion</code> events.
     * a listener that will be notified when a tree node is expanded 
     * or collapsed (a "negative expansion")
     */
    public function addTreeExpansionListener(func:Function, obj:Object):Object {
		return addEventListener(ON_TREE_EXPANDED, func, obj);
    }

    /**
     * Adds a listener for <code>TreeWillExpand</code> events.
     * a listener that will be notified when a tree node will be expanded 
     * or collapsed (a "negative expansion")
     */
    public function addTreeWillExpandListener(func:Function, obj:Object):Object {
		return addEventListener(ON_TREE_WILL_EXPAND, func, obj);
    }
    
    private function firePropertyChange(pn:String, oldValue, newValue):Void{
		dispatchEvent(createEventObj(ON_PROPERTY_CHANGED, pn, oldValue, newValue));
    }

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>path</code> parameter.
     *
     * @param path the <code>TreePath</code> indicating the node that was
     *		expanded
     * @see EventListenerList
     */
    public function fireTreeExpanded(path:TreePath):Void {
		dispatchEvent(createEventObj(ON_TREE_EXPANDED, path));
    }   

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>path</code> parameter.
     *
     * @param path the <code>TreePath</code> indicating the node that was
     *		collapsed
     * @see EventListenerList
     */
    public function fireTreeCollapsed(path:TreePath):Void {
		dispatchEvent(createEventObj(ON_TREE_COLLAPSED, path));
    }   

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>path</code> parameter.
     *
     * @param path the <code>TreePath</code> indicating the node that was
     *		expanded
     * @see EventListenerList
     */
     public function fireTreeWillExpand(path:TreePath):Void{
		dispatchEvent(createEventObj(ON_TREE_WILL_EXPAND, path));
    }   

    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the <code>path</code> parameter.
     *
     * @param path the <code>TreePath</code> indicating the node that was
     *		expanded
     * @see EventListenerList
     */
     public function fireTreeWillCollapse(path:TreePath):Void {
		dispatchEvent(createEventObj(ON_TREE_WILL_COLLAPSE, path));
    }   

    /**
     * Adds a listener for <code>TreeSelection</code> events.
     * the listenerthat will be notified when a node is selected or 
     * deselected (a "negative selection")
     */
    public function addSelectionListener(func:Function, obj:Object):Object {
    	return addEventListener(ON_SELECTION_CHANGED, func, obj);
    }


    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  
     *
     * @param e the <code>TreeSelectionEvent</code> to be fired;
     *          generated by the
     *		<code>TreeSelectionModel</code>
     *          when a node is selected or deselected
     * @see EventListenerList
     */
    private function fireValueChanged(e:TreeSelectionEvent):Void {
		dispatchEvent(createEventObj(ON_SELECTION_CHANGED, e));
    }

    /**
     * Sent when the tree has changed enough that we need to resize
     * the bounds, but not enough that we need to remove the
     * expanded node set (e.g nodes were expanded or collapsed, or
     * nodes were inserted into the tree). You should never have to
     * invoke this, the UI will invoke this as it needs to.
     */
    public function treeDidChange():Void {
    	setViewPosition(restrictionViewPos(getViewPosition()));
        revalidate();
        repaint();
    }

    /**
     * Sets the number of rows that are to be displayed.
     * This will only work if the tree is contained in a 
     * <code>JScrollPane</code>,
     * and will adjust the preferred size and size of that scrollpane.
     *
     * @param newCount the number of rows to display
     */
    public function setVisibleRowCount(newCount:Number):Void {
        var oldCount:Number = visibleRowCount;
		if(oldCount != newCount){
	        visibleRowCount = newCount;
	        firePropertyChange(VISIBLE_ROW_COUNT_PROPERTY, oldCount,
	                           visibleRowCount);
	        invalidate();
		}
    }

    /**
     * Returns the number of rows that are displayed in the display area.
     *
     * @return the number of rows displayed
     */
    public function getVisibleRowCount():Number {
        return visibleRowCount;
    }

    /**
     * Expands the root path, assuming the current TreeModel has been set.
     */
    private function expandRoot():Void {
		var	model:TreeModel = getModel();
	
		if(model != null && model.getRoot() != null) {
		    expandPath(new TreePath([model.getRoot()]));
		}
    }

    /**
     * Returns an array of integers specifying the indexs of the
     * components in the <code>path</code>. If <code>path</code> is
     * the root, this will return an empty array.  If <code>path</code>
     * is <code>null</code>, <code>null</code> will be returned.
     */
    private function getModelIndexsForPath(path:TreePath):Array {
		if(path != null) {
		    var model:TreeModel = getModel();
		    var count:Number = path.getPathCount();
		    var indexs:Array = new int[count - 1];
		    var parent:Object = model.getRoot();
	
		    for(var counter:Number = 1; counter < count; counter++) {
				indexs[counter - 1] = model.getIndexOfChild(parent, path.getPathComponent(counter));
				parent = path.getPathComponent(counter);
				if(indexs[counter - 1] < 0){
				    return null;
				}
			}
			return indexs;
		}
		return null;
    }

    /**
     * Returns a <code>TreePath</code> created by obtaining the children
     * for each of the indices in <code>indexs</code>. If <code>indexs</code>
     * or the <code>TreeModel</code> is <code>null</code>, it will return
     * <code>null</code>.
     */
    private function getPathForIndexs(indexs:Array):TreePath {
		if(indexs == null)
		    return null;
	
		var model:TreeModel = getModel();
	
		if(model == null)
		    return null;
	
		var count:Number = indexs.length;
		var  parent:Object = model.getRoot();
		var parentPath:TreePath = new TreePath([parent]);
	
		for(var counter:Number = 0; counter < count; counter++) {
		    parent = model.getChild(parent, indexs[counter]);
		    if(parent == null){
				return null;
		    }
		    parentPath = parentPath.pathByAddingChild(parent);
		}
		return parentPath;
    }
    
    /**
     * Sets the expanded state of this <code>JTree</code>.
     * If <code>state</code> is
     * true, all parents of <code>path</code> and path are marked as
     * expanded. If <code>state</code> is false, all parents of 
     * <code>path</code> are marked EXPANDED, but <code>path</code> itself
     * is marked collapsed.<p>
     * This will fail if a <code>TreeWillExpandListener</code> vetos it.
     */
    private function setExpandedState(path:TreePath, state:Boolean):Void {
		if(path != null) {
		    // Make sure all parents of path are expanded.
		    var stack:Stack;
		    var parentPath:TreePath = path.getParentPath();
	
		    if (expandedStack.size() == 0) {
				stack = new Stack();
		    }else {
				stack = Stack(expandedStack.pop());
		    }
	
		    try {
				while(parentPath != null) {
				    if(isExpanded(parentPath)) {
						parentPath = null;
				    }else {
						stack.push(parentPath);
						parentPath = parentPath.getParentPath();
				    }
				}
				for(var counter:Number = stack.size() - 1; counter >= 0; counter--) {
				    parentPath = TreePath(stack.pop());
				    if(!isExpanded(parentPath)) {
						try {
						    fireTreeWillExpand(parentPath);
						} catch (eve:Error) {
						    // Expand vetoed!
						    return;
						}
						expandedState.put(parentPath, true);
						fireTreeExpanded(parentPath);
				    }
				}
		    }catch(e:Error) {
				if (expandedStack.size() < TEMP_STACK_SIZE) {
				    stack.clear();
				    expandedStack.push(stack);
				}
		    }
		    if(!state) {
				// collapse last path.
				var cValue:Object = expandedState.get(path);
		
				if(cValue != null && cValue==true) {
				    try {
						fireTreeWillCollapse(path);
				    }catch (eve:Error) {
						return;
				    }
				    expandedState.put(path, false);
				    fireTreeCollapsed(path);
				    if (removeDescendantSelectedPaths(path, false) && !isPathSelected(path)) {
						// A descendant was selected, select the parent.
						addSelectionPath(path);
				    }
				}
		    }else {
				// Expand last path.
				var cValue:Object = expandedState.get(path);
				
				if(cValue == null || !(cValue==true)) {
				    try {
						fireTreeWillExpand(path);
				    }catch (eve:Error) {
						return;
				    }
				    expandedState.put(path, true);
				    fireTreeExpanded(path);
				}
		    }
		}
    }

    /**
     * Returns an <code>Enumeration</code> of <code>TreePaths</code>
     * that have been expanded that
     * are descendants of <code>parent</code>.
     */
    private function getDescendantToggledPaths(parent:TreePath):Array{
		if(parent == null)
		    return null;
	
		var descendants:Array = new Array();
		var nodes:Array = expandedState.keys();
		var path:TreePath;
	
		for(var i:Number=0; i<nodes.length; i++){
		    path = TreePath(nodes[i]);
		    if(parent.isDescendant(path)){
				descendants.push(path);
		    }
		}
		return descendants;
    }
    
    /**
     * Removes any descendants of the <code>TreePaths</code> in
     * <code>toRemove</code>
     * that have been expanded.
     */
	private function removeDescendantToggledPaths(toRemove:Array):Void{
		if(toRemove != null) {
			for(var i:Number=0; i<toRemove.length; i++){
				var descendants:Array = getDescendantToggledPaths(TreePath(toRemove[i]));
				
				if(descendants != null) {
					for(var j:Number=0; j<descendants.length; j++){
						expandedState.remove(descendants[j]);
					}
				}
			}
		}
	}

     /**
      * Clears the cache of toggled tree paths. This does NOT send out
      * any <code>TreeExpansionListener</code> events.
      */
     private function clearToggledPaths():Void {
	 	expandedState.clear();
     }

    /**
     * Removes any paths in the selection that are descendants of
     * <code>path</code>. If <code>includePath</code> is true and
     * <code>path</code> is selected, it will be removed from the selection.
     *
     * @return true if a descendant was selected
     */
    private function removeDescendantSelectedPaths(path:TreePath, includePath:Boolean):Boolean {
		var toRemove:Array = getDescendantSelectedPaths(path, includePath);
	
		if (toRemove != null) {
		    getSelectionModel().removeSelectionPaths(toRemove);
		    return true;
		}
		return false;
    }

    /**
     * Returns an array of paths in the selection that are descendants of
     * <code>path</code>. The returned array may contain <code>null</code>s.
     */
    private function getDescendantSelectedPaths(path:TreePath, includePath:Boolean):Array {
		var sm:TreeSelectionModel = getSelectionModel();
		var selPaths:Array = (sm != null) ? sm.getSelectionPaths() : null;
	
		if(selPaths != null) {
		    var shouldRemove:Boolean = false;
	
		    for(var counter:Number = selPaths.length - 1; counter >= 0; counter--) {
				if(selPaths[counter] != null &&
				   path.isDescendant(selPaths[counter]) &&
				   (!path.equals(selPaths[counter]) || includePath))
				    shouldRemove = true;
				else
				    selPaths[counter] = null;
		    }
		    if(!shouldRemove) {
				selPaths = null;
		    }
		    return selPaths;
		}
		return null;
    }

    /**
     * Removes any paths from the selection model that are descendants of
     * the nodes identified by in <code>e</code>.
     */
    public function removeDescendantSelectedPathsWithEvent(e:TreeModelEvent):Void {
		var pPath:TreePath = e.getTreePath();
		var oldChildren:Array = e.getChildren();
		var sm:TreeSelectionModel = getSelectionModel();
	
		if (sm != null && pPath != null && oldChildren != null && oldChildren.length > 0) {
		    for (var counter:Number = oldChildren.length - 1; counter >= 0; counter--) {
			// Might be better to call getDescendantSelectedPaths
			// numerous times, then push to the model.
			removeDescendantSelectedPaths(pPath.pathByAddingChild(oldChildren[counter]), true);
		    }
		}
    }	

    public function setUIProperty(propertyName:String, value):Void {
        if(propertyName == "rowHeight") {
		    if (!rowHeightSet) {
				setRowHeight(value);
				rowHeightSet = false;
		    }
		}else if(propertyName == "scrollsOnExpand") {
		    if (!scrollsOnExpandSet) {
				setScrollsOnExpand(value);
				scrollsOnExpandSet = false;
		    }
		}else{
		    super.setUIProperty(propertyName, value);
		}
    }	
    

	//*******************************************************************
	//                      Model Selection Listener 
	//*******************************************************************

    /**
     * Invoked by the <code>TreeSelectionModel</code> when the
     * selection changes.
     * 
     * @param source the <code>TreeSelectionModel</code>
     * @param e the <code>TreeSelectionEvent</code> generated by the
     *		<code>TreeSelectionModel</code></code> generated by the
     *		<code>TreeSelectionModel</code>
     */
    private function __valueChangedTreeSelectionRedirector(source:TreeSelectionModel, e:TreeSelectionEvent):Void {
        var newE:TreeSelectionEvent = e.cloneWithSource(this);
        dispatchEvent(createEventObj(ON_SELECTION_CHANGED, newE));
    }

	//*******************************************************************
	//                         TreeModelListener 
	//*******************************************************************

	public function treeNodesChanged(e : TreeModelEvent) : Void {
	}

	public function treeNodesInserted(e : TreeModelEvent) : Void {
	}

	public function treeNodesRemoved(e : TreeModelEvent) : Void {
	    if(e == null){
			return;
	    }

	    var parent:TreePath = e.getTreePath();
	    var children:Array = e.getChildren();

	    if(children == null){
			return;
	    }

	    var rPath:TreePath;
	    var toRemove:Array = new Array();

	    for(var counter:Number = children.length - 1; counter >= 0; counter--) {
			rPath = parent.pathByAddingChild(children[counter]);
			if(expandedState.get(rPath) != null)
			    toRemove.push(rPath);
	    }
	    if(toRemove.length > 0){
			removeDescendantToggledPaths(toRemove);
	    }

	    var model:TreeModel = getModel();

	    if(model == null || model.isLeaf(parent.getLastPathComponent())){
			expandedState.remove(parent);
	    }

	    removeDescendantSelectedPathsWithEvent(e);		
	}

	public function treeStructureChanged(e : TreeModelEvent) : Void {
	    if(e == null){
			return;
	    }

	    // NOTE: If I change this to NOT remove the descendants
	    // and update BasicTreeUIs treeStructureChanged method
	    // to update descendants in response to a treeStructureChanged
	    // event, all the children of the event won't collapse!
	    var parent:TreePath = e.getTreePath();

	    if(parent == null){
			return;
	    }

	    if (parent.getPathCount() == 1) {
			// New root, remove everything!
			clearToggledPaths();
			if(treeModel.getRoot() != null && !treeModel.isLeaf(treeModel.getRoot())) {
			    // Mark the root as expanded, if it isn't a leaf.
			    expandedState.put(parent, true);
			}
	    }else if(expandedState.get(parent) != null) {
			var toRemove:Array = new Array();
			var isExpanded:Boolean = isExpanded(parent);
	
			toRemove.push(parent);
			removeDescendantToggledPaths(toRemove);
			if(isExpanded) {
			    var model:TreeModel = getModel();
	
			    if(model == null || model.isLeaf(parent.getLastPathComponent()))
					collapsePath(parent);
			    else
					expandedState.put(parent, true);
			}
	    }
	    removeDescendantSelectedPaths(parent, false);		
	}
	
	//*******************************************************************
	//                        CellEditorListener
	//*******************************************************************
	
	public function editingStopped(source : CellEditor) : Void {
		var ui:TreeUI = getUI();
		if(ui != null){
			var path:TreePath = ui.getEditingPath(this);
			if(path != null){
				dispatchEvent( createEventObj( ON_CELL_EDITING_STOPPED, path, 
					source.getCellEditorValue(), 
					path.getLastPathComponent() ));
				getModel().valueForPathChanged(getUI().getEditingPath(this), source.getCellEditorValue());
				trace("/d/editingStopped");
			}
		}
		requestFocus();
	}

	public function editingCanceled(source : CellEditor) : Void {
		dispatchEvent(createEventObj( ON_CELL_EDITING_CANCELED, getUI().getEditingPath(this) ) );
		trace("/d/editingCanceled");
		requestFocus();
	}

	
	//*******************************************************************
	//                        Viewportable
	//*******************************************************************
	
	public function getVerticalUnitIncrement() : Number {
		if(verticalUnitIncrement == undefined){
			return getRowHeight();
		}else{
			return verticalUnitIncrement;
		}
	}

	public function getVerticalBlockIncrement() : Number {
		if(verticalBlockIncrement == undefined){
			return Math.max(getRowHeight(), getExtentSize().height - getRowHeight());
		}else{
			return verticalBlockIncrement;
		}
	}

	public function getHorizontalUnitIncrement() : Number {
		if(horizontalUnitIncrement == undefined){
			return 1;
		}else{
			return horizontalUnitIncrement;
		}
	}

	public function getHorizontalBlockIncrement() : Number {
		if(horizontalBlockIncrement == undefined){
			return Math.max(1, getExtentSize().width - 1);
		}else{
			return horizontalBlockIncrement;
		}
	}
	
    public function setVerticalUnitIncrement(increment:Number):Void{
    	if(verticalUnitIncrement != increment){
    		verticalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setVerticalBlockIncrement(increment:Number):Void{
    	if(verticalBlockIncrement != increment){
    		verticalBlockIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalUnitIncrement(increment:Number):Void{
    	if(horizontalUnitIncrement != increment){
    		horizontalUnitIncrement = increment;
			fireStateChanged();
    	}
    }
    
    public function setHorizontalBlockIncrement(increment:Number):Void{
    	if(horizontalBlockIncrement != increment){
    		horizontalBlockIncrement = increment;
			fireStateChanged();
    	}
    }	

	public function setViewportTestSize(s : Dimension) : Void {
		setSize(s);
	}

	public function getExtentSize() : Dimension {
    	return getInsets().getInsideSize(getSize());
	}

	public function getViewSize() : Dimension {
		return getUI().getViewSize();
	}

	public function getViewPosition() : Point {
		return new Point(viewPosition.x, viewPosition.y);
	}

	public function setViewPosition(p : Point) : Void {
		restrictionViewPos(p);
		if(!viewPosition.equals(p)){
			viewPosition.setLocation(p);
			fireStateChanged();
			//TODO r
			//repaint();
		}
	}

	public function scrollRectToVisible(contentRect : Rectangle) : Void {
		var visibleRect:Rectangle = getVisibleRect();
		var ltPoint:Point = contentRect.leftTop();
		var rbPoint:Point = contentRect.rightBottom();
		var ltIn:Boolean = visibleRect.containsPoint(ltPoint);
		var rbIn:Boolean = visibleRect.containsPoint(rbPoint);
		if(ltIn && rbIn){
			return;
		}else{
			if(!ltIn){
				setViewPosition(new Point(ltPoint.x, ltPoint.y));
			}else{
				var extendSize:Dimension = getExtentSize();
				setViewPosition(new Point(rbPoint.x-extendSize.width, rbPoint.y-extendSize.height));
			}
		}
	}
	
	public function getVisibleRect():Rectangle{
		var es:Dimension = getExtentSize();
		return new Rectangle(viewPosition.x, viewPosition.y, es.width, es.height);
	}
	
	private function restrictionViewPos(p:Point):Point{
		var maxPos:Point = getViewMaxPos();
		p.x = Math.max(0, Math.min(maxPos.x, p.x));
		p.y = Math.max(0, Math.min(maxPos.y, p.y));
		return p;
	}
	
	private function getViewMaxPos():Point{
		var showSize:Dimension = getExtentSize();
		var viewSize:Dimension = getViewSize();
		var p:Point = new Point(viewSize.width-showSize.width, viewSize.height-showSize.height);
		if(p.x < 0) p.x = 0;
		if(p.y < 0) p.y = 0;
		return p;
	}

	public function addChangeListener(func : Function, obj : Object) : Object {
		return addEventListener(ON_STATE_CHANGED, func, obj);
	}

	public function getViewportPane() : Component {
		return this;
	}
}