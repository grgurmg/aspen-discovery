import { AlertDialog, Button, Center } from 'native-base';
import React from 'react';
import * as Linking from 'expo-linking';
import {translate} from '../../translations/translations';

export const UpdateAvailable = (props) => {
	const { url, latest, setHasUpdate } = props;
	const [isOpen, setIsOpen] = React.useState(true);
	const onClose = () => {
		setHasUpdate(false);
		setIsOpen(false);
	};
	const cancelRef = React.useRef(null);

	const openAppStore = async () => {
		onClose();
		await Linking.openURL(url);
	}

	return (
		<Center>
			<AlertDialog leastDestructiveRef={cancelRef} isOpen={isOpen} onClose={onClose}>
				<AlertDialog.Content>
					<AlertDialog.Header>{translate('updates.update_available')}</AlertDialog.Header>
					<AlertDialog.Body>{translate('updates.update_message')}</AlertDialog.Body>
					<AlertDialog.Footer>
						<Button.Group space={3}>
							<Button variant="ghost" onPress={onClose} ref={cancelRef}>
								{translate('general.cancel')}
							</Button>
							<Button colorScheme="primary" onPress={() => openAppStore()}>
								{translate('updates.update_now')}
							</Button>
						</Button.Group>
					</AlertDialog.Footer>
				</AlertDialog.Content>
			</AlertDialog>
		</Center>
	);
};