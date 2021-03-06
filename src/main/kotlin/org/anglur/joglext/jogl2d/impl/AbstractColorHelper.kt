/*
 *    Copyright 2016 Jonathan Beaudoin <https://github.com/Jonatino>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.anglur.joglext.jogl2d.impl

import com.jogamp.opengl.GL
import org.anglur.joglext.jogl2d.GLG2DColorHelper
import org.anglur.joglext.jogl2d.GLGraphics2D
import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Paint
import java.awt.RenderingHints.Key
import java.util.*

abstract class AbstractColorHelper : GLG2DColorHelper {
	
	protected lateinit var g2d: GLGraphics2D
	
	private var stack: Deque<ColorState> = ArrayDeque()
	
	private val EMPTY = ColorState()
	override fun setG2D(g2d: GLGraphics2D) {
		this.g2d = g2d
		
		stack.clear()
		stack.push(EMPTY.reset())
	}
	
	override fun push(newG2d: GLGraphics2D) = stack.push(stack.peek().clone())
	
	override fun pop(parentG2d: GLGraphics2D) {
		stack.pop()
		
		// set all the states
		composite = composite
		color = color
		background = background
	}
	
	override fun setHint(key: Key, value: Any?) {
		// nop
	}
	
	override fun resetHints() {
		// nop
	}
	
	override fun dispose() {
	}
	
	/*
	   * Since the destination _always_ covers the entire canvas (i.e. there are
       * always color components for every pixel), some of these composites can
       * be collapsed into each other. They matter when Java2D is drawing into
       * an image and the destination may not take up the entire canvas.
       */// need to pre-multiply the alpha
	override var composite: Composite
		get() = stack.peek().composite!!
		set(comp) {
			val gl = g2d.glContext.gl
			gl.glEnable(GL.GL_BLEND)
			if (comp is AlphaComposite) {
				when (comp.rule) {
					AlphaComposite.SRC, AlphaComposite.SRC_IN -> gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ZERO)
					
					AlphaComposite.SRC_OVER, AlphaComposite.SRC_ATOP -> gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA)
					
					AlphaComposite.SRC_OUT, AlphaComposite.CLEAR -> gl.glBlendFunc(GL.GL_ZERO, GL.GL_ZERO)
					
					AlphaComposite.DST, AlphaComposite.DST_OVER -> gl.glBlendFunc(GL.GL_ZERO, GL.GL_ONE)
					
					AlphaComposite.DST_IN, AlphaComposite.DST_ATOP -> gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_ALPHA)
					
					AlphaComposite.DST_OUT, AlphaComposite.XOR -> gl.glBlendFunc(GL.GL_ZERO, GL.GL_ONE_MINUS_SRC_ALPHA)
				}
				
				stack.peek().composite = comp
				color = color
			} else {
				TODO(if ("setComposite(Composite) with " + comp == null) "null Composite" else comp.javaClass.simpleName)
			}
		}
	
	override var color: Color
		get() = stack.peek().color
		set(c) {
			stack.peek().color = c
			setColorRespectComposite(c)
		}
	
	override var background: Color
		get() = stack.peek().background
		set(color) {
			stack.peek().background = color
		}
	
	override var paint: Paint
		get() = stack.peek().paint!!
		set(paint) {
			stack.peek().paint = paint
		}
	
	private class ColorState : Cloneable {
		var composite = AlphaComposite.SrcOver
		var color = Color.WHITE
		var paint: Paint? = null
		var background = Color.WHITE
		
		public override fun clone(): ColorState {
			try {
				return super.clone() as ColorState
			} catch (e: CloneNotSupportedException) {
				throw AssertionError(e)
			}
			
		}
		
		fun reset() = apply {
			composite = AlphaComposite.SrcOver
			color = Color.WHITE
			paint = null
			background = Color.WHITE
		}
		
	}
	
}