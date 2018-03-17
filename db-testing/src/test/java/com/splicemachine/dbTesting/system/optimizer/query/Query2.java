/*
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Some parts of this source code are based on Apache Derby, and the following notices apply to
 * Apache Derby:
 *
 * Apache Derby is a subproject of the Apache DB project, and is licensed under
 * the Apache License, Version 2.0 (the "License"); you may not use these files
 * except in compliance with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Splice Machine, Inc. has modified the Apache Derby code in this file.
 *
 * All such Splice Machine modifications are Copyright 2012 - 2018 Splice Machine, Inc.,
 * and are licensed to you under the GNU Affero General Public License.
 */
package com.splicemachine.dbTesting.system.optimizer.query;

/**
 * 
 * Class Query2: Returns a list of queries that Selects from multiple views
 * using joins
 * 
 */

public class Query2 extends GenericQuery {

	public Query2() {
		description = "Select from multiple views using joins";
		generateQueries();
	}

	/**
	 */
	public void generateQueries() {
		queries
				.add("select xx.col1,xx.col2,xx.col3,xx.col5,xx.col6 from v8 xx, v8_2 xy where xx.col4=xy.col4 and xy.col7 in ('MYTABLE32_COL7:4122','MYTABLE32_COL7:3419','MYTABLE1_COL7:2197','MYTABLE1_COL7:1243','MYTABLE2_COL7:3684','MYTABLE2_COL7:4264','MYTABLE3_COL7:450','MYTABLE3_COL7:2150','MYTABLE4_COL7:966','MYTABLE4_COL7:2812','MYTABLE5_COL7:4897','MYTABLE5_COL7:2748','MYTABLE6_COL7:245','MYTABLE6_COL7:2341','MYTABLE7_COL7:1603','MYTABLE7_COL7:1150','MYTABLE8_COL7:4759','MYTABLE8_COL7:1535','MYTABLE9_COL7:1227','MYTABLE9_COL7:229','MYTABLE10_COL7:549','MYTABLE10_COL7:4043','MYTABLE11_COL7:1400','MYTABLE11_COL7:3964','MYTABLE12_COL7:3141','MYTABLE12_COL7:2808','MYTABLE13_COL7:2008','MYTABLE13_COL7:3835','MYTABLE14_COL7:3897','MYTABLE14_COL7:246','MYTABLE15_COL7:1284','MYTABLE15_COL7:3715','MYTABLE16_COL7:2583','MYTABLE16_COL7:4507','MYTABLE17_COL7:2899','MYTABLE17_COL7:1670','MYTABLE18_COL7:2187','MYTABLE18_COL7:175','MYTABLE19_COL7:3783','MYTABLE19_COL7:1525','MYTABLE20_COL7:3398','MYTABLE20_COL7:1568','MYTABLE21_COL7:3148','MYTABLE21_COL7:2262','MYTABLE22_COL7:2815','MYTABLE22_COL7:2413','MYTABLE23_COL7:746','MYTABLE23_COL7:4357','MYTABLE24_COL7:1361','MYTABLE24_COL7:564','MYTABLE25_COL7:1427','MYTABLE25_COL7:1568','MYTABLE26_COL7:3707','MYTABLE26_COL7:1986','MYTABLE27_COL7:2771','MYTABLE27_COL7:3322','MYTABLE28_COL7:4485','MYTABLE28_COL7:3905','MYTABLE29_COL7:4142','MYTABLE29_COL7:3812','MYTABLE30_COL7:2724','MYTABLE30_COL7:1380','MYTABLE31_COL7:3702','MYTABLE31_COL7:803' )");
		queries
				.add("select xx.col1,xx.col2,xx.col3,xx.col5,xx.col6 from v16 xx, v16_2 xy where xx.col4=xy.col4 and xy.col7 in ('MYTABLE32_COL7:4122','MYTABLE32_COL7:3419','MYTABLE1_COL7:2197','MYTABLE1_COL7:1243','MYTABLE2_COL7:3684','MYTABLE2_COL7:4264','MYTABLE3_COL7:450','MYTABLE3_COL7:2150','MYTABLE4_COL7:966','MYTABLE4_COL7:2812','MYTABLE5_COL7:4897','MYTABLE5_COL7:2748','MYTABLE6_COL7:245','MYTABLE6_COL7:2341','MYTABLE7_COL7:1603','MYTABLE7_COL7:1150','MYTABLE8_COL7:4759','MYTABLE8_COL7:1535','MYTABLE9_COL7:1227','MYTABLE9_COL7:229','MYTABLE10_COL7:549','MYTABLE10_COL7:4043','MYTABLE11_COL7:1400','MYTABLE11_COL7:3964','MYTABLE12_COL7:3141','MYTABLE12_COL7:2808','MYTABLE13_COL7:2008','MYTABLE13_COL7:3835','MYTABLE14_COL7:3897','MYTABLE14_COL7:246','MYTABLE15_COL7:1284','MYTABLE15_COL7:3715','MYTABLE16_COL7:2583','MYTABLE16_COL7:4507','MYTABLE17_COL7:2899','MYTABLE17_COL7:1670','MYTABLE18_COL7:2187','MYTABLE18_COL7:175','MYTABLE19_COL7:3783','MYTABLE19_COL7:1525','MYTABLE20_COL7:3398','MYTABLE20_COL7:1568','MYTABLE21_COL7:3148','MYTABLE21_COL7:2262','MYTABLE22_COL7:2815','MYTABLE22_COL7:2413','MYTABLE23_COL7:746','MYTABLE23_COL7:4357','MYTABLE24_COL7:1361','MYTABLE24_COL7:564','MYTABLE25_COL7:1427','MYTABLE25_COL7:1568','MYTABLE26_COL7:3707','MYTABLE26_COL7:1986','MYTABLE27_COL7:2771','MYTABLE27_COL7:3322','MYTABLE28_COL7:4485','MYTABLE28_COL7:3905','MYTABLE29_COL7:4142','MYTABLE29_COL7:3812','MYTABLE30_COL7:2724','MYTABLE30_COL7:1380','MYTABLE31_COL7:3702','MYTABLE31_COL7:803' )");
		queries
				.add("select xx.col1,xx.col2,xx.col3,xx.col5,xx.col6 from v32 xx, v32_2 xy where xx.col4=xy.col4 and xy.col7 in ('MYTABLE32_COL7:4122','MYTABLE32_COL7:3419','MYTABLE1_COL7:2197','MYTABLE1_COL7:1243','MYTABLE2_COL7:3684','MYTABLE2_COL7:4264','MYTABLE3_COL7:450','MYTABLE3_COL7:2150','MYTABLE4_COL7:966','MYTABLE4_COL7:2812','MYTABLE5_COL7:4897','MYTABLE5_COL7:2748','MYTABLE6_COL7:245','MYTABLE6_COL7:2341','MYTABLE7_COL7:1603','MYTABLE7_COL7:1150','MYTABLE8_COL7:4759','MYTABLE8_COL7:1535','MYTABLE9_COL7:1227','MYTABLE9_COL7:229','MYTABLE10_COL7:549','MYTABLE10_COL7:4043','MYTABLE11_COL7:1400','MYTABLE11_COL7:3964','MYTABLE12_COL7:3141','MYTABLE12_COL7:2808','MYTABLE13_COL7:2008','MYTABLE13_COL7:3835','MYTABLE14_COL7:3897','MYTABLE14_COL7:246','MYTABLE15_COL7:1284','MYTABLE15_COL7:3715','MYTABLE16_COL7:2583','MYTABLE16_COL7:4507','MYTABLE17_COL7:2899','MYTABLE17_COL7:1670','MYTABLE18_COL7:2187','MYTABLE18_COL7:175','MYTABLE19_COL7:3783','MYTABLE19_COL7:1525','MYTABLE20_COL7:3398','MYTABLE20_COL7:1568','MYTABLE21_COL7:3148','MYTABLE21_COL7:2262','MYTABLE22_COL7:2815','MYTABLE22_COL7:2413','MYTABLE23_COL7:746','MYTABLE23_COL7:4357','MYTABLE24_COL7:1361','MYTABLE24_COL7:564','MYTABLE25_COL7:1427','MYTABLE25_COL7:1568','MYTABLE26_COL7:3707','MYTABLE26_COL7:1986','MYTABLE27_COL7:2771','MYTABLE27_COL7:3322','MYTABLE28_COL7:4485','MYTABLE28_COL7:3905','MYTABLE29_COL7:4142','MYTABLE29_COL7:3812','MYTABLE30_COL7:2724','MYTABLE30_COL7:1380','MYTABLE31_COL7:3702','MYTABLE31_COL7:803' )");
		queries
				.add("select xx.col1,xx.col2,xx.col3,xx.col5,xx.col6 from v42 xx, v42_2 xy where xx.col4=xy.col4 and xy.col7 in ('MYTABLE32_COL7:4122','MYTABLE32_COL7:3419','MYTABLE1_COL7:2197','MYTABLE1_COL7:1243','MYTABLE2_COL7:3684','MYTABLE2_COL7:4264','MYTABLE3_COL7:450','MYTABLE3_COL7:2150','MYTABLE4_COL7:966','MYTABLE4_COL7:2812','MYTABLE5_COL7:4897','MYTABLE5_COL7:2748','MYTABLE6_COL7:245','MYTABLE6_COL7:2341','MYTABLE7_COL7:1603','MYTABLE7_COL7:1150','MYTABLE8_COL7:4759','MYTABLE8_COL7:1535','MYTABLE9_COL7:1227','MYTABLE9_COL7:229','MYTABLE10_COL7:549','MYTABLE10_COL7:4043','MYTABLE11_COL7:1400','MYTABLE11_COL7:3964','MYTABLE12_COL7:3141','MYTABLE12_COL7:2808','MYTABLE13_COL7:2008','MYTABLE13_COL7:3835','MYTABLE14_COL7:3897','MYTABLE14_COL7:246','MYTABLE15_COL7:1284','MYTABLE15_COL7:3715','MYTABLE16_COL7:2583','MYTABLE16_COL7:4507','MYTABLE17_COL7:2899','MYTABLE17_COL7:1670','MYTABLE18_COL7:2187','MYTABLE18_COL7:175','MYTABLE19_COL7:3783','MYTABLE19_COL7:1525','MYTABLE20_COL7:3398','MYTABLE20_COL7:1568','MYTABLE21_COL7:3148','MYTABLE21_COL7:2262','MYTABLE22_COL7:2815','MYTABLE22_COL7:2413','MYTABLE23_COL7:746','MYTABLE23_COL7:4357','MYTABLE24_COL7:1361','MYTABLE24_COL7:564','MYTABLE25_COL7:1427','MYTABLE25_COL7:1568','MYTABLE26_COL7:3707','MYTABLE26_COL7:1986','MYTABLE27_COL7:2771','MYTABLE27_COL7:3322','MYTABLE28_COL7:4485','MYTABLE28_COL7:3905','MYTABLE29_COL7:4142','MYTABLE29_COL7:3812','MYTABLE30_COL7:2724','MYTABLE30_COL7:1380','MYTABLE31_COL7:3702','MYTABLE31_COL7:803' )");

	}

}
