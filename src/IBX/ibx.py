import re
import urllib.request
from bs4 import BeautifulSoup
from bs4.element import Tag, NavigableString, Comment

# === Variables of Interest ===
#
# info       -- dictionary containing all text from the Varicose Veins CPB
# codes      -- dictionary containing CPT codes for Varicose Veins
# title, policy_number
# references -- list of references for Varicose Veins
# version_effective, version_issued, version_reissued

# === Initial Information ===

payer = 'IBX'

# === All CPB's of IBX ===

ibx_url = 'http://medpolicy.ibx.com'
base_url = 'http://medpolicy.ibx.com/policies/mpi.nsf/InternetMedicalPoliciesByTitleIBC!OpenView&Start=1&Count=2000&CollapseView'

resp = urllib.request.urlopen(base_url)
bulletin = BeautifulSoup(resp, from_encoding=resp.info().get_param('charset'))

cpb_links = []

for link in bulletin.find_all('a', href=True):
    cpb_links.append(link)

# Note: Need to clean up cpb_links since it contains a lot extra or non-CPB related links

# === Varicose Veins Example ===

sample = cpb_links[553] # <--- TODO: get cpb info from cpb_links
sample_url = ibx_url + sample.attrs['href']
sample_name = sample.getText()

sample_resp = urllib.request.urlopen(sample_url)
sample_soup = BeautifulSoup(sample_resp, from_encoding=sample_resp.info().get_param('charset'))

sample_tables = sample_soup.findAll('table')

# === All Headers in web page ===

headers = sample_soup.findAll('td', {'class' : 'IBXHeaderPol'}) # each header belongs to the 'IBXHeaderPol' class
info = {}

# The idea here is to read all information until the next IBXHeaderPol
for header in headers:
    header_name = header.getText().strip()
    info[header_name] = str()
    next_elements = header.nextGenerator()
    for elt in next_elements:
        if type(elt) is NavigableString:
            info[header_name] += elt
        elif type(elt) is not Comment and elt.has_key('class') and elt['class'] == ['IBXHeaderPol']:
            break

# === CPT Procedure Code Number (s) ===
# TODO : Use info['coding']; REFACTOR. This section needs to change.

cpt_table = sample_tables[22] # <--- TODO very bad!! don't hard code
cpt_text = cpt_table.getText().splitlines()

digits = re.compile('([0-9]+)(,[0-9]+)*')

codes = {}

for idx, line in enumerate(cpt_text):
    if 'MEDICALLY  NECESSARY' in line.strip():
        codes['MEDICALLY NECESSARY'] = list(map(lambda x: x[0], re.findall(digits, cpt_text[idx+1])))
    if line == 'COSMETIC':
        # There is no \n after the COSMETIC section in this webpage.
        # The hack here relies on us knowing that there is only one code for COSMETIC
        codes['COSMETIC'] = list(map(lambda x: x[0], re.findall(digits, cpt_text[idx+1])))[0]
    if 'EXPERIMENTAL/INVESTIGATIONAL' in line.strip():
        codes['EXPERIMENTAL/INVESTIGATIONAL'] = list(map(lambda x: x[0], re.findall(digits, cpt_text[idx+1])))
        
# === Get Title and Policy Number ===
# The title and policy info are contained in a table with 'class' = 'codingtable'

from_colon = lambda s: s[s.find(':')+1:]
title_policy_info = sample_soup.findAll('table', {'class': 'codingtable'})
title = from_colon(title_policy_info[0].getText().strip())
policy_number = from_colon(title_policy_info[1].getText().strip())

# === Version Details ===
# consider that these could also be N/A
# variables names are too long... Change

version_effective_regex = re.compile('Version Effective Date:\s+(\d+/\d+/\d+)')
version_issued_regex = re.compile('Version Issued Date:\s+(\d+/\d+/\d+)')
version_reissued_regex = re.compile('Version Reissued Date:(.*)')

for line in info['Policy History'].splitlines():
    ve = re.findall(version_effective_regex, line)
    vi = re.findall(version_issued_regex, line)
    vr = re.findall(version_reissued_regex, line)
    if ve != []:
        version_effective = ve
    if vi != []:
        version_issued = vi
    if vr != []:
        version_reissued = vr

# === References ===
# remove unnecessary white space and get a list of references
# TODO: readability
references = list(filter(None,info['References'][1:].strip().splitlines()))


# === Printing out information ===

print("================================================================================")
print("Varicose Veins IBX")
print("Link: ")
print("http://medpolicy.ibx.com/policies/mpi.nsf/f12d23cb982d59b485257bad00552d87/be933dcd7cd9c7958525825d004bc7c2!OpenDocument")
print("================================================================================")
print()
print("================================================================================")
print("Title and Policy Number: ")
print("================================================================================")
print(title)
print(policy_number)

print()
print("================================================================================")
print("CPT Codes")
print("================================================================================")
print("Medically Necessary")
print(codes['MEDICALLY NECESSARY'])
print("Experimental/Investigational")
print(codes['EXPERIMENTAL/INVESTIGATIONAL'])
print("Cosmetic")
print(codes['COSMETIC'])

print()
print("================================================================================")
print("Version Information")
print("================================================================================")
print("Version Effective Date")
print(version_effective)
print("Version Issued Date")
print(version_issued)
print("Version Reissued Date")
print(version_reissued)

print()
print("================================================================================")
print("References")
print("================================================================================")
print(references)

print()
print("================================================================================")
print("Full String Dump")
print("================================================================================")
for elt in info:
    print(elt, '-->', info[elt])
