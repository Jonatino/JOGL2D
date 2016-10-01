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

package org.anglur.joglext.jogl2d.shape

import org.anglur.joglext.jogl2d.shape.impl.*
import java.awt.Shape
import java.awt.geom.*

/**
 * Created by Jonathan on 9/30/2016.
 */
object ShapeIterator {
	
	fun get(shape: Shape): PathIterator {
		when (shape) {
			is Ellipse2D -> return EllipseIterator(shape)
			is RoundRectangle2D -> return RoundRectIterator(shape)
			is Arc2D -> return ArcIterator(shape)
			is Rectangle2D -> return RectIterator(shape)
			is Line2D -> return LineIterator(shape)
		}
		throw RuntimeException("Unknown shape! ${shape.javaClass.simpleName}")
	}
	
}
