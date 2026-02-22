import xml.etree.ElementTree as ET
import sys

def search_entities(file_path):
    target_names = ["Ordine", "Prodotto", "FilieraPoint", "Utente", "Carrello", "OrderLine"]
    found_entities = {name: [] for name in target_names}
    
    try:
        context = ET.iterparse(file_path, events=("start", "end"))
        
        for event, elem in context:
            if event == "start":
                # Check attributes for target names
                for k, v in elem.attrib.items():
                    for name in target_names:
                        if name in v:
                            # Capture element tag and modelType if present
                            info = f"Tag: {elem.tag}, ModelType: {elem.attrib.get('modelType', 'N/A')}, Attr: {k}={v}"
                            found_entities[name].append(info)
                
                # Check child properties (common in VP VP)
                if elem.tag == 'property' and elem.attrib.get('name') == 'name':
                    val = elem.attrib.get('value')
                    for name in target_names:
                         if val and name in val:
                            info = f"Tag: property (name), Value: {val}"
                            found_entities[name].append(info)

            if event == "end":
                elem.clear()

        for name, findings in found_entities.items():
            print(f"--- Results for '{name}' ---")
            unique_findings = set(findings) # Deduplicate
            for f in list(unique_findings)[:10]: # Limit output
                print(f)
            if not unique_findings:
                print("No matches found.")
            print("\n")

    except Exception as e:
        print(f"Error parsing XMI: {e}")

if __name__ == "__main__":
    search_entities("Progetto visual.xmi")
