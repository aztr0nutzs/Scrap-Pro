package com.example.domain.model

data class KnowledgeSection(val title: String, val content: String)

object KnowledgeBaseData {
    val sections = listOf(
        KnowledgeSection(
            title = "The Magnet Rule & Taxonomy",
            content = "Ferrous vs. Non-Ferrous:\n\nIf a magnet sticks to it, it is a Ferrous metal (contains iron). Examples include steel, cast iron, and sheet iron. These are typically the lowest-paying metals (sold by the ton or for a few cents a pound).\n\nIf a magnet DOES NOT stick, it is a Non-Ferrous metal. Examples include copper, brass, aluminum, and stainless steel. These are the highest-paying metals. Always separate your non-ferrous metals from your ferrous metals to maximize profit. Mixed loads will often be bought at the lowest grade price (the ferrous price)."
        ),
        KnowledgeSection(
            title = "High-Value Metal Deep Dive",
            content = "Bare Bright Copper: The holy grail. Must be clean, unalloyed, uncoated copper wire thicker than 16 gauge. No insulation, no paint, no solder, no tarnish.\n\n#1 Copper: Clean copper tubing and wire, free of brass fittings, solder, or paint. Can have some oxidation but no green corrosion.\n\n#2 Copper: Copper with solder, paint, or light tin coating. Also includes burnt wire or wire with thin hair-like strands.\n\nBrass (Red & Yellow): Yellow brass is common in plumbing fixtures and keys. Red brass has more copper and pays slightly more (common in water meters and older valves).\n\nCast vs. Sheet Aluminum: Cast aluminum is brittle and breaks when bent (e.g., engine blocks, BBQ grills). Sheet aluminum bends easily (e.g., siding, lawn chairs). Cast usually pays slightly more than sheet.\n\nStainless Steel: 304 and 316 are the most common non-magnetic stainless steels (sinks, kegs, appliances). Always test with a magnet—if it sticks strongly, it's magnetic stainless (400 series) and pays significantly less."
        ),
        KnowledgeSection(
            title = "Appliance Teardown & Processing Workflows",
            content = "Washers & Dryers: Cut the power cord (copper wire). Remove the back panel. Extract the electric motor (contains copper windings, though often sold as 'electric motors' grade unless broken open). Look for brass fittings on water inlets.\n\nMicrowaves: Cut the power cord. Open the casing. Remove the transformer (heavy, contains copper or aluminum windings—check by scratching). Remove the small fan motor and any insulated wire. WARNING: Do not mess with the capacitor unless safely discharged.\n\nWater Heaters: Strip the outer metal shell and insulation if easy. Remove the brass drain valve at the bottom. The inner tank is steel.\n\nElectric Motors: Can be sold whole. To maximize profit, break the cast iron or aluminum shell, cut one side of the copper windings with an angle grinder or sawzall, and pry the copper out from the other side. This upgrades it to #2 Copper."
        ),
        KnowledgeSection(
            title = "Sourcing & Free Scrap Acquisition",
            content = "Contractor Relationships: Plumbers, electricians, and HVAC techs generate high-value scrap daily but often lack the time to run it to the yard. Offer a reliable, scheduled pickup service. You can offer a profit-share (e.g., 50/50 split on copper/brass) or charge a small removal fee if they just want it gone.\n\nCurbside Scouting: Check local bulk trash pickup schedules. Drive neighborhoods the night before. Look for appliances, lawnmowers, and metal furniture. Always be respectful and don't make a mess.\n\nDumpster Access: Never take from a commercial dumpster without explicit permission from the business owner. Establishing a good relationship and offering to keep the area clean can secure exclusive rights to their metal waste."
        ),
        KnowledgeSection(
            title = "Safety, Tools & Freon Compliance",
            content = "Essential Tool Kit:\n- Angle grinder (with cutoff wheels)\n- Strong neodymium magnet (for testing)\n- Wire strippers & snips\n- Sawzall (reciprocating saw)\n- Ratchet straps & bungee cords\n- 55-gallon drums or heavy-duty bins for sorting\n- PPE: Thick leather gloves, safety glasses, steel-toe boots.\n\nHazardous Materials & Compliance:\n- Freon (EPA Section 608): It is illegal and environmentally harmful to vent refrigerant (Freon) from AC units, refrigerators, or dehumidifiers into the atmosphere. You must use a certified recovery machine to extract it, or have a certified technician do it before scrapping the unit.\n- Batteries: Lead-acid batteries (car/truck) are bought by scrap yards, but handle with care to avoid acid spills. Lithium-ion batteries (tools, electronics) are a major fire hazard and must be recycled at specialized facilities, NOT thrown in general scrap metal."
        )
    )
}
